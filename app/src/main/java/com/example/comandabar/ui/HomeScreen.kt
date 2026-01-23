package com.example.comandabar.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.comandabar.ui.components.Footer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController
) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // TopAppBar ORIGINAL
            Surface(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Menu do Bar",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                    ) {}
                }
            }

            // Menu ORIGINAL
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                MenuItem(
                    title = "Gerenciar Clientes",
                    subtitle = "Cadastrar e editar clientes",
                    icon = Icons.Default.Person,
                    onClick = { navController.navigate("clientes") }
                )

                MenuItem(
                    title = "Gerenciar Categorias",
                    subtitle = "Cadastrar e editar categorias",
                    icon = Icons.Default.Category,
                    onClick = { navController.navigate("categorias") }
                )

                MenuItem(
                    title = "Gerenciar Produtos",
                    subtitle = "Adicionar e editar produtos",
                    icon = Icons.Default.LocalBar,
                    onClick = { navController.navigate("produtos") }
                )

                MenuItem(
                    title = "Gerar Comanda",
                    subtitle = "Criar uma comanda para cliente",
                    icon = Icons.AutoMirrored.Filled.ReceiptLong, // ✅ corrigido
                    onClick = { navController.navigate("gerar_comanda") }
                )

                MenuItem(
                    title = "Ver Comandas Ativas",
                    subtitle = "Visualizar comandas abertas",
                    icon = Icons.AutoMirrored.Filled.ListAlt, // ✅ corrigido
                    onClick = { navController.navigate("lista_comandas") }
                )

                MenuItem(
                    title = "Relatórios",
                    subtitle = "Visualizar vendas e histórico",
                    icon = Icons.Default.BarChart,
                    onClick = { navController.navigate("relatorios") }
                )
            }

            // Rodapé ORIGINAL
            Footer()
        }
    }
}

@Composable
private fun MenuItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}
