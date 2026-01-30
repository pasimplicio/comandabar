package com.example.comandabar.bar.ui.qrcode

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.comandabar.shared.repository.ComandaRepository
import com.example.comandabar.shared.repository.ModuleRepository
import com.example.comandabar.ui.components.Footer
import com.example.comandabar.ui.components.FooterItem
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions

@Composable
fun QrCodeScreen(navController: NavController) {
    val context = LocalContext.current
    val comandas by ComandaRepository.comandas.collectAsState()
    val selectedModule by ModuleRepository.selectedModule.collectAsState()

    val scanLauncher = rememberLauncherForActivityResult(
        contract = ScanContract(),
        onResult = { result: ScanIntentResult ->
            if (result.contents == null) {
                Toast.makeText(context, "Leitura cancelada", Toast.LENGTH_SHORT).show()
            } else {
                val qrCodeData = result.contents.trim()
                val comanda = ComandaRepository.getComandaPorQrCode(qrCodeData)

                if (comanda != null) {
                    ComandaRepository.selecionarComanda(comanda.id)
                    navController.navigate("cliente_comanda")
                } else {
                    Toast.makeText(
                        context,
                        "Comanda não encontrada! Verifique o QR Code.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    )

    val footerItems = listOf(
        FooterItem("Home", Icons.Default.Home) {
            navController.navigate("home")
        },
        /*        FooterItem("Menu", Icons.Default.RestaurantMenu) {
                    ModuleRepository.limparSelecao()
                    navController.navigate("home")
                }*/
    )

    Scaffold(
        bottomBar = { Footer(items = footerItems) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    val options = ScanOptions().apply {
                        setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                        setPrompt("Posicione o QR Code da comanda")
                        setCameraId(0)
                        setBeepEnabled(true)
                        setBarcodeImageEnabled(true)
                    }
                    scanLauncher.launch(options)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null)
                    Text("Escanear QR Code da Comanda")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Suas comandas",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (comandas.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                        contentDescription = "Nenhuma comanda",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = "Você ainda não tem comandas salvas.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(comandas) { comanda ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            onClick = {
                                ComandaRepository.selecionarComanda(comanda.id)
                                navController.navigate("cliente_comanda")
                            }
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = comanda.cliente.nome,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                                Text(
                                    text = "Itens: ${comanda.itens.size} • Total: R$ ${"%.2f".format(comanda.total)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = "Status: ${if (comanda.isAberta()) "Aberta" else "Fechada"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = "Código: ${comanda.id.take(8)}...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
