package com.example.comandabar.bar.ui.menu

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.comandabar.ui.components.Footer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarMenuScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Menu do Bar") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        },
        bottomBar = { Footer() }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            MenuItem("Gerenciar Clientes", Icons.Default.People) {
                navController.navigate("clientes")
            }

            MenuItem("Gerenciar Categorias", Icons.Default.Category) {
                navController.navigate("categorias")
            }

            MenuItem("Gerenciar Produtos", Icons.Default.Inventory) {
                navController.navigate("produtos")
            }


            MenuItem("Gerar Comanda", Icons.AutoMirrored.Filled.ReceiptLong) {
                navController.navigate("gerar_comanda")
            }

            MenuItem("Ver Comandas Ativas", Icons.AutoMirrored.Filled.ReceiptLong) {
                navController.navigate("lista_comandas")
            }

            MenuItem("Relatórios", Icons.Default.Assessment) {
                navController.navigate("relatorios")
            }
        }
    }
}

@Composable
private fun MenuItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null)
            Spacer(modifier = Modifier.width(16.dp))
            Text(title)
        }
    }
}
