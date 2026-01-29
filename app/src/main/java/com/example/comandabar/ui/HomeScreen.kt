package com.example.comandabar.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.WineBar
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.comandabar.R
import com.example.comandabar.shared.repository.AppModule
import com.example.comandabar.shared.repository.ModuleRepository
import com.example.comandabar.ui.components.Footer

@Composable
fun HomeScreen(navController: NavController) {
    val selectedModule by ModuleRepository.selectedModule.collectAsState()
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // HEADER COM LOGO
            Surface(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo ComandaBar",
                        modifier = Modifier.size(96.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "ComandaBar",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    )

                    Text(
                        text = "Controle inteligente de comandas",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                        )
                    )
                }
            }

            // CONTEÚDO
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                when (selectedModule) {
                    AppModule.CLIENTE -> {
                        ModuleCard(
                            title = "Módulo Cliente",
                            subtitle = "Escanear QR Code da Comanda",
                            icon = Icons.Default.QrCodeScanner
                        ) {
                            navController.navigate("qrcode")
                        }
                    }
                    AppModule.BAR -> {
                        ModuleCard(
                            title = "Módulo Bar",
                            subtitle = "Gerenciar comandas e produtos",
                            icon = Icons.Default.WineBar
                        ) {
                            navController.navigate("bar_menu")
                        }
                    }
                    null -> {
                        ModuleCard(
                            title = "Módulo Cliente",
                            subtitle = "Escanear QR Code da Comanda",
                            icon = Icons.Default.QrCodeScanner
                        ) {
                            ModuleRepository.selecionarModulo(AppModule.CLIENTE)
                            navController.navigate("qrcode")
                        }

                        ModuleCard(
                            title = "Módulo Bar",
                            subtitle = "Gerenciar comandas e produtos",
                            icon = Icons.Default.WineBar
                        ) {
                            ModuleRepository.selecionarModulo(AppModule.BAR)
                            navController.navigate("bar_menu")
                        }
                    }
                }

                if (selectedModule != null) {
                    TextButton(
                        onClick = { ModuleRepository.limparSelecao() },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Trocar módulo")
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "Sistema de comandas digital - v1.0",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            Footer()
        }
    }
}

@Composable
private fun ModuleCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null)

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall)
            }

            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
        }
    }
}
