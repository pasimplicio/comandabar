package com.example.comandabar.bar.ui.comanda

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.comandabar.bar.viewmodel.ComandaViewModel
import com.example.comandabar.shared.model.Comanda
import com.example.comandabar.shared.model.ItemComanda
import com.example.comandabar.ui.components.Footer
import com.example.comandabar.ui.components.FooterItem
import com.example.comandabar.ui.theme.CorCerveja
import com.example.comandabar.ui.theme.CorEnergetico
import com.example.comandabar.ui.theme.CorRefrigerante
import com.example.comandabar.ui.theme.CorSuco
import com.example.comandabar.ui.theme.CorWhisky
import com.example.comandabar.extensions.formatDouble
import kotlinx.coroutines.launch

@Composable
fun ComandaBarScreen(
    navController: NavController,
    onBack: () -> Unit = { navController.popBackStack() },
    viewModel: ComandaViewModel = viewModel()
) {
    val context = LocalContext.current
    val comanda by viewModel.comandaSelecionada.collectAsState()

    var showQrCodeDialog by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showCloseComandaDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // TopAppBar personalizada usando Surface
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
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            IconButton(
                                onClick = onBack,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Voltar",
                                    tint = Color.White
                                )
                            }

                            Column {
                                Text(
                                    text = "Comanda",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color.White
                                )
                                Text(
                                    text = comanda?.cliente?.nome ?: "Carregando...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }

                        IconButton(
                            onClick = { showQrCodeDialog = true },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                Icons.Default.QrCode,
                                contentDescription = "QR Code",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    // Linha decorativa
                    Surface(
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                    ) {}
                }
            }

            // Conteúdo principal
            if (comanda == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
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
                    // Status da comanda
                    ComandaStatusCard(comanda!!)

                    // Itens da comanda
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Itens Consumidos",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            if (comanda!!.itens.isEmpty()) {
                                Text(
                                    text = "Nenhum item consumido ainda",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            } else {
                                LazyColumn(
                                    modifier = Modifier.heightIn(max = 250.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(comanda!!.itens) { item ->
                                        ItemComandaRow(item)
                                    }
                                }
                            }
                        }
                    }

                    // Total
                    TotalCard(comanda!!)

                    // Botões de ação
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { showConfirmDialog = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Adicionar")
                        }

                        Button(
                            onClick = { showCloseComandaDialog = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            enabled = comanda!!.status == Comanda.Status.ABERTA
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Fechar")
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Footer(
                        items = listOf(
                            FooterItem("Home", Icons.Default.Home) {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            FooterItem("Menu", Icons.Default.RestaurantMenu) {
                                navController.navigate("bar_menu")
                            },
                            FooterItem("Relatórios", Icons.Default.BarChart) {
                                navController.navigate("relatorios")
                            }
                        )
                    )
                }
            }
        }

        // SnackbarHost - CORRIGIDO: Remover align e usar Box
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }

// Dialog QR Code - ATUALIZADO
    if (showQrCodeDialog && comanda != null) {
        AlertDialog(
            onDismissRequest = { showQrCodeDialog = false },
            title = {
                Text("QR Code da Comanda")
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // QR Code REAL
                    Card(
                        modifier = Modifier
                            .size(250.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            // Usar o componente de QR Code real
                            com.example.comandabar.utils.QrCodeImage(
                                data = comanda!!.qrCodeData,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    // Informações da comanda
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Código:",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = comanda!!.qrCodeData,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Cliente: ${comanda!!.cliente.nome}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "ID: ${comanda!!.id.take(8)}...",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    Text(
                        text = "O cliente pode escanear este QR Code para ver o consumo em tempo real.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showQrCodeDialog = false }
                ) {
                    Text("Fechar")
                }
            }
        )
    }

    // Dialog confirmação adicionar item
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = {
                Text("Adicionar Item")
            },
            text = {
                Text("Deseja ir para a tela de produtos para adicionar itens nesta comanda?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        // Navegar para produtos com ID da comanda selecionada
                        navController.navigate("produtos_bar")
                    }
                ) {
                    Text("Sim")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showConfirmDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Dialog fechar comanda
    if (showCloseComandaDialog && comanda != null) {
        AlertDialog(
            onDismissRequest = { showCloseComandaDialog = false },
            title = {
                Text("Fechar Comanda")
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Tem certeza que deseja fechar esta comanda?")
                    Text(
                        // CORRIGIDO: Usar total direto da comanda
                        text = "Total: R$ ${comanda!!.total.formatDouble(2)}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showCloseComandaDialog = false
                        viewModel.fecharComanda(comanda!!.id)
                        showSuccessDialog = true

                        scope.launch {
                            snackbarHostState.showSnackbar("Comanda fechada com sucesso!")
                        }
                    }
                ) {
                    Text("Fechar Comanda")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showCloseComandaDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Dialog sucesso
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = {
                Text("Sucesso!")
            },
            text = {
                Text("A comanda foi fechada com sucesso.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSuccessDialog = false
                        navController.popBackStack()
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun ComandaStatusCard(comanda: Comanda) {
    val statusColor = if (comanda.status == Comanda.Status.ABERTA) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.secondary
    }

    val statusText = if (comanda.status == Comanda.Status.ABERTA) {
        "ABERTA"
    } else {
        "FECHADA"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = statusColor.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Status",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                )
            }

            Icon(
                imageVector = if (comanda.status == Comanda.Status.ABERTA)
                    Icons.Default.LockOpen else Icons.Default.Lock,
                contentDescription = null,
                tint = statusColor,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun ItemComandaRow(item: ItemComanda) {
    val emoji = item.produto.emoji
    val nome = item.produto.nome
    val quantidade = item.quantidade
    val preco = item.produto.preco
    val totalItem = quantidade * preco

    val corCategoria = when (emoji) {
        "🍺" -> CorCerveja
        "🥃" -> CorWhisky
        "🥤" -> CorRefrigerante
        "🧃" -> CorSuco
        "⚡" -> CorEnergetico
        else -> MaterialTheme.colorScheme.primary
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = emoji,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = nome,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
                Text(
                    text = "Qtd: $quantidade",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "R$ ${totalItem.formatDouble(2)}",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = corCategoria
            )
            Text(
                text = "R$ ${preco.formatDouble(2)} un",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun TotalCard(comanda: Comanda) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Total",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            // CORRIGIDO: Usar total direto da comanda
            Text(
                text = "R$ ${comanda.total.formatDouble(2)}",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@Composable
fun SimpleQrCodeVisual(
    qrCodeData: String,
    modifier: Modifier = Modifier
) {
    // Usar o componente real de QR Code
    com.example.comandabar.utils.QrCodeImage(
        data = qrCodeData,
        modifier = modifier
    )
}