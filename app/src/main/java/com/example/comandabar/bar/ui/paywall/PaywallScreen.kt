package com.example.comandabar.ui.paywall

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun PaywallScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Período gratuito encerrado",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Para continuar utilizando o ComandaBar, é necessário assinar o plano.",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    // Fase 2: iniciar fluxo de assinatura
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Assinar agora")
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = {
                    // Fase 2: restaurar compra
                }
            ) {
                Text("Restaurar compra")
            }
        }
    }
}
