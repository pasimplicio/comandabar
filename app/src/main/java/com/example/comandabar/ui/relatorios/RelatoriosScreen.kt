package com.example.comandabar.ui.relatorios

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.comandabar.shared.repository.ComandaRepository
import com.example.comandabar.ui.components.Footer
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RelatoriosScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val comandasFechadas by ComandaRepository.comandas.collectAsState()
    var clienteFiltro by remember { mutableStateOf("") }
    var dataInicialFiltro by remember { mutableStateOf("") }
    var dataFinalFiltro by remember { mutableStateOf("") }

    val dataInicialMillis = remember(dataInicialFiltro) {
        parseDateMillis(dataInicialFiltro, isEndDate = false)
    }
    val dataFinalMillis = remember(dataFinalFiltro) {
        parseDateMillis(dataFinalFiltro, isEndDate = true)
    }

    val comandasFiltradas = comandasFechadas
        .filter { it.status == com.example.comandabar.shared.model.Comanda.Status.FECHADA }
        .filter { comanda ->
            val dataFechamento = comanda.fechadaEm ?: comanda.criadaEm
            val clienteOk = clienteFiltro.isBlank() ||
                    comanda.cliente.nome.contains(clienteFiltro.trim(), ignoreCase = true)
            val dataInicialOk = dataInicialMillis == null || dataFechamento >= dataInicialMillis
            val dataFinalOk = dataFinalMillis == null || dataFechamento <= dataFinalMillis
            clienteOk && dataInicialOk && dataFinalOk
        }

    val totalVendas = comandasFiltradas.sumOf { it.total }
    val totalComandas = comandasFiltradas.size

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
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
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    IconButton(
                                        onClick = { navController.popBackStack() },
                                        modifier = Modifier.size(48.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Voltar",
                                            tint = Color.White,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }

                                    Text(
                                        text = "Relatórios",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
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

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Filtros",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                                OutlinedTextField(
                                    value = clienteFiltro,
                                    onValueChange = { clienteFiltro = it },
                                    label = { Text("Cliente") },
                                    placeholder = { Text("Nome do cliente") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    textStyle = MaterialTheme.typography.bodySmall
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    OutlinedTextField(
                                        value = dataInicialFiltro,
                                        onValueChange = { dataInicialFiltro = it },
                                        label = { Text("Data inicial") },
                                        placeholder = { Text("dd/MM/yyyy") },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true,
                                        textStyle = MaterialTheme.typography.bodySmall
                                    )
                                    OutlinedTextField(
                                        value = dataFinalFiltro,
                                        onValueChange = { dataFinalFiltro = it },
                                        label = { Text("Data final") },
                                        placeholder = { Text("dd/MM/yyyy") },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true,
                                        textStyle = MaterialTheme.typography.bodySmall
                                    )
                                }
                                Text(
                                    text = "Formato das datas: dd/MM/yyyy",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                        }

                        // Cards de resumo
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        "Total de Vendas",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        "R$ ${"%.2f".format(totalVendas)}",
                                        style = MaterialTheme.typography.headlineSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    )
                                }
                            }

                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        "Comandas Fechadas",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        totalComandas.toString(),
                                        style = MaterialTheme.typography.headlineSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = {
                                exportarRelatorioPdf(
                                    context = context,
                                    comandas = comandasFiltradas,
                                    totalVendas = totalVendas,
                                    totalComandas = totalComandas
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Exportar relatório em PDF")
                        }

                        Text(
                            "Histórico de Comandas Fechadas",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        if (comandasFiltradas.isEmpty()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(40.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ReceiptLong,
                                    contentDescription = "Nenhuma comanda fechada",
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                    modifier = Modifier.size(96.dp)
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Text(
                                    "Nenhuma comanda fechada",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "As comandas fechadas aparecerão aqui",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(bottom = 16.dp)
                            ) {
                                items(comandasFiltradas.sortedByDescending { it.fechadaEm }) { comanda ->
                                    ComandaRelatorioCard(comanda = comanda)
                                }
                            }
                        }
                    }
                }
            }

            // Rodapé
            Footer()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComandaRelatorioCard(comanda: com.example.comandabar.shared.model.Comanda) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Comanda #${comanda.id.take(8)}...",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = "Cliente: ${comanda.cliente.nome}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Text(
                    text = "R$ ${"%.2f".format(comanda.total)}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Itens: ${comanda.itens.size}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = "Fechada: ${formatDate(comanda.fechadaEm ?: comanda.criadaEm)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            if (comanda.itens.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Itens:",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    comanda.itens.take(3).forEach { item ->
                        Text(
                            text = "• ${item.produto.nome} x${item.quantidade} - R$ ${"%.2f".format(item.subtotal)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                    if (comanda.itens.size > 3) {
                        Text(
                            text = "... e mais ${comanda.itens.size - 3} itens",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

private fun parseDateMillis(dateText: String, isEndDate: Boolean): Long? {
    if (dateText.isBlank()) return null
    return try {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val parsedDate = sdf.parse(dateText.trim()) ?: return null
        val calendar = Calendar.getInstance().apply {
            time = parsedDate
            if (isEndDate) {
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            } else {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
        }
        calendar.timeInMillis
    } catch (exception: Exception) {
        null
    }
}

private fun exportarRelatorioPdf(
    context: Context,
    comandas: List<com.example.comandabar.shared.model.Comanda>,
    totalVendas: Double,
    totalComandas: Int
) {
    val document = PdfDocument()
    var pageNumber = 1
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, pageNumber).create()
    var page = document.startPage(pageInfo)
    var canvas = page.canvas
    val paint = Paint()
    var yPosition = 40
    val xPosition = 40
    val logoBitmap = BitmapFactory.decodeResource(context.resources, com.example.comandabar.R.drawable.logo)
    val maxLogoWidth = 56
    val maxLogoHeight = 56
    val logoWidth = min(logoBitmap.width, maxLogoWidth)
    val logoHeight = min(logoBitmap.height, maxLogoHeight)
    val scaledLogo = Bitmap.createScaledBitmap(logoBitmap, logoWidth, logoHeight, true)

    canvas.drawRect(
        24f,
        24f,
        (pageInfo.pageWidth - 24).toFloat(),
        120f,
        paint.apply {
            color = android.graphics.Color.parseColor("#F5F0EE")
            style = Paint.Style.FILL
        }
    )
    canvas.drawBitmap(scaledLogo, 40f, 40f, null)
    paint.color = android.graphics.Color.parseColor("#3E2723")
    paint.textSize = 20f
    paint.isFakeBoldText = true
    canvas.drawText("ComandaBar", 112f, 64f, paint)
    paint.textSize = 12f
    paint.isFakeBoldText = false
    canvas.drawText("Relatório de comandas fechadas", 112f, 84f, paint)
    paint.textSize = 11f
    paint.color = android.graphics.Color.parseColor("#6D4C41")
    val dataGeracao = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
    canvas.drawText("Gerado em: $dataGeracao", 112f, 102f, paint)

    yPosition = 150
    paint.color = android.graphics.Color.BLACK
    paint.textSize = 12f
    canvas.drawText("Total de vendas: R$ ${"%.2f".format(totalVendas)}", xPosition.toFloat(), yPosition.toFloat(), paint)
    yPosition += 18
    canvas.drawText("Comandas fechadas: $totalComandas", xPosition.toFloat(), yPosition.toFloat(), paint)
    yPosition += 24

    paint.isFakeBoldText = true
    canvas.drawText("Detalhes:", xPosition.toFloat(), yPosition.toFloat(), paint)
    paint.isFakeBoldText = false
    yPosition += 18

    if (comandas.isEmpty()) {
        canvas.drawText("Nenhuma comanda encontrada.", xPosition.toFloat(), yPosition.toFloat(), paint)
    } else {
        comandas.forEach { comanda ->
            val linha = "Comanda ${comanda.id.take(8)} • ${comanda.cliente.nome} • " +
                    "R$ ${"%.2f".format(comanda.total)} • ${formatDate(comanda.fechadaEm ?: comanda.criadaEm)}"
            canvas.drawText(linha, xPosition.toFloat(), yPosition.toFloat(), paint)
            yPosition += 16
            if (yPosition > pageInfo.pageHeight - 40) {
                document.finishPage(page)
                pageNumber += 1
                val nextPageInfo = PdfDocument.PageInfo.Builder(595, 842, pageNumber).create()
                page = document.startPage(nextPageInfo)
                canvas = page.canvas
                yPosition = 40
            }
        }
    }

    document.finishPage(page)

    val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
    val fileName = "relatorio_comandas_${dateFormat.format(Date())}.pdf"
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
    }

    val resolver = context.contentResolver
    val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
    if (uri == null) {
        document.close()
        Toast.makeText(context, "Não foi possível salvar o PDF.", Toast.LENGTH_LONG).show()
        return
    }

    try {
        resolver.openOutputStream(uri)?.use { outputStream ->
            document.writeTo(outputStream)
        }
        Toast.makeText(
            context,
            "PDF salvo em Downloads: $fileName",
            Toast.LENGTH_LONG
        ).show()
    } catch (exception: Exception) {
        Toast.makeText(context, "Erro ao salvar PDF: ${exception.localizedMessage}", Toast.LENGTH_LONG).show()
    } finally {
        document.close()
    }
}
