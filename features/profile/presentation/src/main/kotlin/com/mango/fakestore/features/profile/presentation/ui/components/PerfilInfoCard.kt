package com.mango.fakestore.features.profile.presentation.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.mango.fakestore.core.designsystem.component.MangoCard
import com.mango.fakestore.core.designsystem.component.MangoCardVariant
import com.mango.fakestore.core.designsystem.component.MangoDivider
import com.mango.fakestore.core.designsystem.component.MangoText
import com.mango.fakestore.core.designsystem.theme.MangoColorTokens
import com.mango.fakestore.core.designsystem.theme.MangoSpacing
import com.mango.fakestore.core.designsystem.theme.MangoTextStyles
import com.mango.fakestore.core.designsystem.theme.MangoTheme
import com.mango.fakestore.features.profile.presentation.R
import com.mango.fakestore.features.profile.presentation.model.PerfilContenidoUi

@Suppress("LongMethod")
@Composable
fun PerfilInfoCard(
    usuario: PerfilContenidoUi,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(horizontal = MangoSpacing.md)) {
        MangoText(
            text = usuario.nombreCompleto,
            style = MangoTextStyles.headlineMedium,
            modifier = Modifier.padding(vertical = MangoSpacing.sm),
        )
        MangoText(
            text = "@${usuario.nombreUsuario}",
            style = MangoTextStyles.bodyMedium,
            color = MangoColorTokens.onSurfaceVariant,
        )

        Spacer(Modifier.height(MangoSpacing.lg))

        MangoCard(
            variant = MangoCardVariant.Outlined,
            modifier = Modifier.fillMaxWidth(),
        ) {
            InfoRow(
                etiqueta = stringResource(R.string.perfil_campo_email),
                valor = usuario.email,
            )
            MangoDivider()
            InfoRow(
                etiqueta = stringResource(R.string.perfil_campo_telefono),
                valor = usuario.telefono,
            )
        }

        Spacer(Modifier.height(MangoSpacing.md))

        MangoCard(
            variant = MangoCardVariant.Outlined,
            modifier = Modifier.fillMaxWidth(),
        ) {
            InfoRow(
                etiqueta = stringResource(R.string.perfil_campo_calle),
                valor = usuario.calle,
            )
            MangoDivider()
            InfoRow(
                etiqueta = stringResource(R.string.perfil_campo_ciudad),
                valor = usuario.ciudad,
            )
            MangoDivider()
            InfoRow(
                etiqueta = stringResource(R.string.perfil_campo_codigo_postal),
                valor = usuario.codigoPostal,
            )
        }

        Spacer(Modifier.height(MangoSpacing.md))

        MangoCard(
            variant = MangoCardVariant.Outlined,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = MangoSpacing.sm),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MangoText(
                    text = stringResource(R.string.perfil_seccion_actividad),
                    style = MangoTextStyles.labelLarge,
                    color = MangoColorTokens.onSurfaceVariant,
                )
                MangoText(
                    text = if (usuario.contadorFavoritos == 1) {
                        stringResource(R.string.perfil_favoritos_contador_uno)
                    } else {
                        stringResource(R.string.perfil_favoritos_contador, usuario.contadorFavoritos)
                    },
                    style = MangoTextStyles.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun InfoRow(
    etiqueta: String,
    valor: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = MangoSpacing.xs),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        MangoText(
            text = etiqueta,
            style = MangoTextStyles.labelMedium,
            color = MangoColorTokens.onSurfaceVariant,
        )
        MangoText(
            text = valor,
            style = MangoTextStyles.bodySmall,
        )
    }
}

// region Previews

private val sampleUsuario = PerfilContenidoUi(
    id = 8,
    nombreCompleto = "John Doe",
    nombreUsuario = "johnd",
    email = "john@example.com",
    telefono = "1-570-236-7033",
    ciudad = "kilcoole",
    calle = "new road 7835",
    codigoPostal = "12926-3874",
    contadorFavoritos = 5,
)

@Preview(name = "PerfilInfoCard - Claro", showBackground = true)
@Composable
private fun PerfilInfoCardClaroPreview() {
    MangoTheme { PerfilInfoCard(usuario = sampleUsuario) }
}

@Preview(name = "PerfilInfoCard - Oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PerfilInfoCardOscuroPreview() {
    MangoTheme { PerfilInfoCard(usuario = sampleUsuario) }
}

@Preview(name = "PerfilInfoCard sin favoritos - Claro", showBackground = true)
@Composable
private fun PerfilInfoCardSinFavoritosPreview() {
    MangoTheme { PerfilInfoCard(usuario = sampleUsuario.copy(contadorFavoritos = 0)) }
}

@Preview(name = "PerfilInfoCard un favorito - Claro", showBackground = true)
@Composable
private fun PerfilInfoCardUnFavoritoPreview() {
    MangoTheme { PerfilInfoCard(usuario = sampleUsuario.copy(contadorFavoritos = 1)) }
}

// endregion
