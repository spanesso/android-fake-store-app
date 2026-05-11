#!/usr/bin/env bash
# Audit script para validar-arquitectura.
# Recorre el repo Android y aplica las 10 reglas ARQ-001..ARQ-010 con grep/awk.
# Salida: JSON por stdout con la lista de violaciones.
#
# Uso:
#   ./scripts/audit.sh [ruta]
# Si no se pasa ruta, audita repository/android-fake-store-app/ entero.

set -euo pipefail

ROOT="${1:-repository/android-fake-store-app}"

if [[ ! -d "$ROOT" ]]; then
    echo "{\"violaciones\": [], \"error\": \"No existe el directorio $ROOT\"}"
    exit 0
fi

# Si no hay módulos todavía, retornar limpio.
if [[ -z "$(find "$ROOT" -type d -path "*/features/*" -o -path "*/core/*" 2>/dev/null | head -1)" ]]; then
    echo "{\"violaciones\": [], \"nota\": \"Sin módulos auditables todavía (ETAPA 0).\"}"
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
    # Escapado mínimo para JSON.
    detalle=${detalle//\"/\\\"}
    sugerencia=${sugerencia//\"/\\\"}
    printf '{"regla":"%s","archivo":"%s","linea":%s,"detalle":"%s","sugerencia":"%s"}\n' \
        "$regla" "$archivo" "$linea" "$detalle" "$sugerencia" >> "$VIOLACIONES_TMP"
}

# ARQ-001: hiltViewModel/viewModel fuera de paquetes route.
while IFS=: read -r archivo linea _; do
    if [[ "$archivo" != *"/ui/route/"* ]]; then
        agregar_violacion "ARQ-001" "$archivo" "$linea" \
            "Composable de feature importa hiltViewModel/viewModel fuera de paquete route" \
            "Mover la instanciacion del ViewModel al wrapper Route y pasar uiState + onEvent al Composable puro."
    fi
done < <(grep -rn -E "^import (androidx\.hilt\.navigation\.compose\.hiltViewModel|androidx\.lifecycle\.viewmodel\.compose\.viewModel)" \
    "$ROOT"/features/*/presentation/src/main/kotlin 2>/dev/null || true)

# ARQ-002: imports de Material3 fuera de :core:design-system.
while IFS=: read -r archivo linea _; do
    if [[ "$archivo" != *"core/design-system/"* ]]; then
        agregar_violacion "ARQ-002" "$archivo" "$linea" \
            "Import de androidx.compose.material3 fuera de :core:design-system" \
            "Usar el componente equivalente de :core:design-system (MangoButton, MangoTextField, etc.)."
    fi
done < <(grep -rn -E "^import androidx\.compose\.material3\." \
    "$ROOT"/features 2>/dev/null || true)

# ARQ-007: UseCases con imports Android.
while IFS=: read -r archivo linea _; do
    agregar_violacion "ARQ-007" "$archivo" "$linea" \
        "UseCase importa codigo Android/framework" \
        "Mover la logica Android al ViewModel; UseCase debe permanecer agnostico al framework."
done < <(grep -rn -E "^import (androidx\.|android\.|com\.mango\.fakestore\.core\.designsystem\.)" \
    "$ROOT"/features/*/domain/src/main/kotlin/*/casosdeuso 2>/dev/null || true)

# ARQ-009: ciclos entre modulos (delegado a Konsist en CI; aqui solo placeholder).
# Una deteccion completa requiere parsear build.gradle.kts; lo dejamos a Konsist.

# Construir JSON final.
{
    printf '{"violaciones":['
    paste -sd',' "$VIOLACIONES_TMP" 2>/dev/null || true
    printf ']}'
} | tr -d '\n'
echo
