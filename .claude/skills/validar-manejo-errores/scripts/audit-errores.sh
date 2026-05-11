#!/usr/bin/env bash
# Audit script para validar-manejo-errores.
# Aplica las reglas ERR-003, ERR-004, ERR-005, ERR-006 y ERR-007 con grep/awk.
# Las reglas ERR-001, ERR-002, ERR-008, ERR-009 y ERR-010 requieren analisis
# sintactico mas profundo y se delegan al razonamiento del modelo + Detekt en CI.
#
# Uso:
#   ./scripts/audit-errores.sh [ruta]
# Salida: JSON por stdout con la lista de violaciones.

set -euo pipefail

ROOT="${1:-repository/android-fake-store-app}"

if [[ ! -d "$ROOT" ]]; then
    echo "{\"violaciones\":[],\"error\":\"No existe el directorio $ROOT\"}"
    exit 0
fi

if [[ -z "$(find "$ROOT" -type d \( -path "*/features/*" -o -path "*/core/*" \) 2>/dev/null | head -1)" ]]; then
    echo "{\"violaciones\":[],\"nota\":\"Sin código auditable todavía (ETAPA 0).\"}"
    exit 0
fi

VIOLACIONES_TMP=$(mktemp)
trap 'rm -f "$VIOLACIONES_TMP"' EXIT

agregar_violacion() {
    local regla="$1"
    local archivo="$2"
    local linea="$3"
    local detalle="$4"
    local sugerencia="$5"
    detalle=${detalle//\"/\\\"}
    sugerencia=${sugerencia//\"/\\\"}
    printf '{"regla":"%s","archivo":"%s","linea":%s,"detalle":"%s","sugerencia":"%s"}\n' \
        "$regla" "$archivo" "$linea" "$detalle" "$sugerencia" >> "$VIOLACIONES_TMP"
}

# ERR-003: throwable.message o .message en Composables.
while IFS=: read -r archivo linea contenido; do
    if [[ "$archivo" == *"/presentation/"* ]] && [[ "$archivo" == *"/ui/"* ]]; then
        agregar_violacion "ERR-003" "$archivo" "$linea" \
            "Composable lee .message de Throwable/DomainError directamente" \
            "Usar stringResource(uiError.messageRes) via MangoErrorState/MangoSnackbar; mapear DomainError -> UiError en el ViewModel."
    fi
done < <(grep -rn -E "\.message" \
    "$ROOT"/features/*/presentation/src/main/kotlin/*/ui 2>/dev/null || true)

# ERR-004: imports prohibidos en Composables (DomainError o Throwable).
while IFS=: read -r archivo linea _; do
    if [[ "$archivo" == *"/presentation/"* ]] && [[ "$archivo" == *"/ui/"* ]]; then
        agregar_violacion "ERR-004" "$archivo" "$linea" \
            "Composable importa DomainError o Throwable" \
            "La UI solo conoce UiError; mover la traduccion al ViewModel."
    fi
done < <(grep -rn -E "^import (com\.mango\.fakestore\.core\.error\.DomainError|java\.lang\.Throwable)" \
    "$ROOT"/features/*/presentation/src/main/kotlin 2>/dev/null || true)

# ERR-005: catch genérico fuera de barreras (whitelist por sufijo de archivo).
while IFS=: read -r archivo linea _; do
    # Whitelist:
    if [[ "$archivo" == *"ErrorMapper.kt" ]]; then continue; fi
    if [[ "$archivo" == *"/data/error/"*"Mapper.kt" ]]; then continue; fi
    if [[ "$archivo" == *"/core/error/"*".kt" ]]; then continue; fi
    if [[ "$archivo" == *"Initializer.kt" ]]; then continue; fi
    if [[ "$archivo" == *"/app/"*"App.kt" ]]; then continue; fi
    agregar_violacion "ERR-005" "$archivo" "$linea" \
        "catch (e: Exception/Throwable) generico fuera de barrera permitida" \
        "Mover la captura a un ErrorMapper o CoroutineExceptionHandler raiz, y anotar con @SuppressWithReason si es estrictamente necesario."
done < <(grep -rn -E "catch[[:space:]]*\([[:space:]]*[a-zA-Z_]+[[:space:]]*:[[:space:]]*(Exception|Throwable)[[:space:]]*\)" \
    "$ROOT" --include="*.kt" 2>/dev/null || true)

# ERR-006: runCatching sin .fold inmediato (heurística: línea con runCatching no termina en .fold(.
while IFS=: read -r archivo linea contenido; do
    if [[ "$contenido" != *".fold("* ]]; then
        agregar_violacion "ERR-006" "$archivo" "$linea" \
            "runCatching sin .fold que produzca Either" \
            "Sustituir por safeApiCall/safeDbCall o por runCatching { ... }.fold(onSuccess = { it.right() }, onFailure = { errorMapper.mapException<T>(it) })."
    fi
done < <(grep -rn -E "runCatching[[:space:]]*\{" \
    "$ROOT" --include="*.kt" 2>/dev/null || true)

# ERR-007: strings hardcoded en MangoErrorState/MangoSnackbar/AlertDialog/MangoDialog
# (heurística: parametros que contienen literal entre comillas).
while IFS=: read -r archivo linea contenido; do
    agregar_violacion "ERR-007" "$archivo" "$linea" \
        "Posible string hardcoded en MangoErrorState/MangoSnackbar/dialogo" \
        "Usar stringResource(R.string.error_<dominio>_<causa>); crear la cadena en res/values/strings.xml y res/values-en/strings.xml."
done < <(grep -rn -E "(MangoErrorState|MangoSnackbar|MangoDialog|AlertDialog)\s*\([^)]*\"[^\"]+\"" \
    "$ROOT" --include="*.kt" 2>/dev/null || true)

{
    printf '{"violaciones":['
    paste -sd',' "$VIOLACIONES_TMP" 2>/dev/null || true
    printf ']}'
} | tr -d '\n'
echo
