package com.example.comandabar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.comandabar.bar.repository.CategoriaRepository
import com.example.comandabar.bar.repository.ProdutoRepository
import com.example.comandabar.cliente.repository.ClienteRepository
import com.example.comandabar.navigation.NavGraph
import com.example.comandabar.shared.repository.ComandaRepository
import com.example.comandabar.shared.repository.ModuleRepository
import com.example.comandabar.ui.theme.ComandaBarTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ClienteRepository.init(this)
        ComandaRepository.init(this)
        ProdutoRepository.init(this)
        CategoriaRepository.init(this)
        ModuleRepository.init(this)

        setContent {
            ComandaBarTheme {
                val navController = rememberNavController()
                NavGraph(navController)
            }
        }
    }
}
