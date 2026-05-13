package com.example.fakestoreapp.ui

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.fakestoreapp.R
import com.example.fakestoreapp.ui.navigation.MangoNavHost
import com.mango.fakestore.core.designsystem.component.MangoDialog
import com.mango.fakestore.core.designsystem.theme.MangoTheme
import com.mango.fakestore.core.security.integrity.IntegrityPolicy
import com.mango.fakestore.core.ui.MangoOfflineBanner

@Composable
fun MainContent(
    viewModel: AppViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
) {
    val isOffline by viewModel.isOffline.collectAsStateWithLifecycle()
    val contadorFavoritos by viewModel.contadorFavoritos.collectAsStateWithLifecycle()
    val startDestination by viewModel.startDestination.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val context = LocalContext.current
    val activity = context as? Activity
    val snackbarHostState = remember { SnackbarHostState() }

    var mostrarDialogoBloqueo by remember { mutableStateOf(false) }
    var mostrarDialogoAdvertencia by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val resultado = viewModel.integridadResultado
        if (resultado.estaComprometido) {
            when (resultado.politica) {
                IntegrityPolicy.BLOCK -> mostrarDialogoBloqueo = true
                IntegrityPolicy.WARN -> mostrarDialogoAdvertencia = true
                IntegrityPolicy.LOG -> Unit
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { efecto ->
            when (efecto) {
                is AppUiEffect.MostrarErrorGlobal ->
                    snackbarHostState.showSnackbar(context.getString(efecto.uiError.messageRes))
            }
        }
    }

    if (mostrarDialogoBloqueo) {
        MangoDialog(
            title = context.getString(R.string.integridad_titulo),
            text = context.getString(R.string.integridad_mensaje_bloqueo),
            confirmLabel = context.getString(R.string.integridad_boton_salir),
            onConfirm = { activity?.finish() },
            onDismiss = { activity?.finish() },
        )
    }

    if (mostrarDialogoAdvertencia) {
        MangoDialog(
            title = context.getString(R.string.integridad_titulo),
            text = context.getString(R.string.integridad_mensaje_advertencia),
            confirmLabel = context.getString(R.string.integridad_boton_continuar),
            onConfirm = { mostrarDialogoAdvertencia = false },
            onDismiss = { activity?.finish() },
            dismissLabel = context.getString(R.string.integridad_boton_salir),
        )
    }

    MangoTheme {
        Box(modifier = modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (isOffline) {
                    MangoOfflineBanner()
                }
                MangoNavHost(
                    navController = navController,
                    startDestination = startDestination,
                    onMostrarSnackbar = { msg -> snackbarHostState.showSnackbar(msg) },
                    contadorFavoritos = contadorFavoritos,
                    modifier = Modifier.weight(1f),
                )
            }
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}
