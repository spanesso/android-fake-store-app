package com.example.fakestoreapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.fakestoreapp.ui.theme.FakestoreappTheme
import com.mango.fakestore.features.products.presentation.ui.route.ProductosRoute
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FakestoreappTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
                    ProductosRoute(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}
