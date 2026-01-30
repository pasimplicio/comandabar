package com.example.comandabar.bar.ui.comanda

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.comandabar.bar.viewmodel.ComandaViewModel
import com.example.comandabar.shared.model.ItemComanda
import com.example.comandabar.ui.components.Footer
import com.example.comandabar.ui.components.FooterItem
import com.example.comandabar.extensions.formatDouble
import com.example.comandabar.ui.components.QrCodeCard

@Composable
fun ComandaBarScreen(
    navController: NavController,
    onBack: () -> Unit = { navController.popBackStack() },
    viewModel: ComandaViewModel = viewModel()
) {
    val comanda by viewModel.comandaSelecionada.collectAsState()

    var showConfirmDialog by remember { mutableStateOf(false) }
    var showQrCodeDialog by remember { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // TopAppBar
            Surface(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = onBack) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Voltar",
                                    tint = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    text = "Comanda",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color.White
                                )
                                Text(
                                    text = comanda?.cliente?.nome ?: "",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }

                        IconButton(
                            onClick = { showQrCodeDialog = true },
                            enabled = comanda != null
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCode,
                                contentDescription = "Mostrar QR Code",
                                tint = Color.White
                            )
                        }
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                    ) {}
                }
            }

            if (comanda == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    // Itens
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Itens Consumidos",
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            if (comanda!!.itens.isEmpty()) {
                                Text("Nenhum item consumido")
                            } else {
                                LazyColumn {
                                    items(comanda!!.itens) { item ->
                                        ItemComandaRow(item)
                                    }
                                }
                            }
                        }
                    }

                    // Total
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total", fontWeight = FontWeight.Bold)
                            Text(
                                "R$ ${comanda!!.total.formatDouble(2)}",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Botões
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { showConfirmDialog = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Adicionar itens")
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Footer(
                        items = listOf(
                            FooterItem("Home", Icons.Default.Home) {
                                navController.navigate("home")
                            },
/*                            FooterItem("Menu", Icons.Default.RestaurantMenu) {
                                navController.navigate("produtos")
                            }*/
                        )
                    )
                }
            }
        }
    }

    // Dialog confirmação
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Adicionar Item") },
            text = { Text("Deseja incluir itens nesta comanda?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        navController.navigate("produtos")
                    }
                ) {
                    Text("Sim")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showQrCodeDialog && comanda != null) {
        AlertDialog(
            onDismissRequest = { showQrCodeDialog = false },
            title = { Text("QR Code da Comanda") },
            text = {
                QrCodeCard(
                    qrCodeData = comanda!!.qrCodeData,
                    clientName = comanda!!.cliente.nome,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = { showQrCodeDialog = false }) {
                    Text("Fechar")
                }
            }
        )
    }
}

@Composable
fun ItemComandaRow(item: ItemComanda) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("${item.produto.nome} x${item.quantidade}")
        Text("R$ ${item.subtotal.formatDouble(2)}")
    }
}
