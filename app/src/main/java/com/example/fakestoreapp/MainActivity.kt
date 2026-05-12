package com.example.fakestoreapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.fakestoreapp.ui.AppViewModel
import com.example.fakestoreapp.ui.MainContent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val appViewModel: AppViewModel by viewModels()

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        appViewModel.reportarErrorGlobal(throwable)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        lifecycleScope.launch(errorHandler) { }
        setContent {
            MainContent()
        }
    }
}
