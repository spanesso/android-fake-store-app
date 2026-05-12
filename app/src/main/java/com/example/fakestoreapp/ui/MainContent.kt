package com.example.fakestoreapp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.fakestoreapp.ui.navigation.MangoNavHost
import com.mango.fakestore.core.designsystem.theme.MangoTheme
import com.mango.fakestore.core.ui.MangoOfflineBanner

@Composable
fun MainContent(
    viewModel: AppViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
) {
    val isOffline by viewModel.isOffline.collectAsStateWithLifecycle()
    val contadorFavoritos by viewModel.contadorFavoritos.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { efecto ->
            when (efecto) {
                is AppUiEffect.MostrarErrorGlobal ->
                    snackbarHostState.showSnackbar(context.getString(efecto.uiError.messageRes))
            }
        }
    }

    MangoTheme {
        Box(modifier = modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (isOffline) {
                    MangoOfflineBanner()
                }
                MangoNavHost(
                    navController = navController,
                    sesionAutenticada = viewModel.sesionAutenticada,
                    onAutenticarPerfil = viewModel::autenticarParaPerfil,
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
