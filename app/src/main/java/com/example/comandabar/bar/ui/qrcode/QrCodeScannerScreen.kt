package com.example.comandabar.bar.ui.qrcode

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.comandabar.shared.repository.ComandaRepository
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions

@Composable
fun QrCodeScannerScreen(
    navController: NavController
) {
    val context = LocalContext.current

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

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
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
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null)
                Text("Escanear QR Code da Comanda")
            }
        }

        Text(
            text = "Ou",
            modifier = Modifier.padding(vertical = 16.dp),
            style = MaterialTheme.typography.bodyMedium
        )

        OutlinedButton(
            onClick = {
                navController.navigate("qrcode_scanner_simple")
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Usar Scanner Simples")
        }
    }
}
