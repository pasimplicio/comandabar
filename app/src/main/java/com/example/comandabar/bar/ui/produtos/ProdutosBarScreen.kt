package com.example.comandabar.bar.ui.produtos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.comandabar.bar.model.Produto
import com.example.comandabar.bar.viewmodel.ProdutoViewModel
import com.example.comandabar.shared.repository.ComandaRepository
import com.example.comandabar.ui.components.Footer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProdutosBarScreen(
    onBack: () -> Unit,
    onAdicionarProduto: () -> Unit,
    onEditarProduto: (String) -> Unit,
    produtoViewModel: ProdutoViewModel = viewModel()
) {
    val produtos by produtoViewModel.produtos.collectAsState()
    var produtoParaExcluir by remember { mutableStateOf<Produto?>(null) }

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
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
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    IconButton(onClick = onBack) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Voltar",
                                            tint = Color.White
                                        )
                                    }

                                    Text(
                                        text = "Produtos",
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

                    if (produtos.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.LocalBar,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                modifier = Modifier.size(96.dp)
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                "Nenhum produto cadastrado",
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentPadding = PaddingValues(bottom = 80.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(produtos) { produto ->
                                ProdutoCard(
                                    produto = produto,
                                    onEditClick = { onEditarProduto(produto.id) },
                                    onDeleteClick = { produtoParaExcluir = produto },
                                    onAddToComanda = {
                                        // ✅ CORREÇÃO: adiciona na comanda ativa
                                        ComandaRepository.adicionarProduto(produto)
                                        // ✅ COMPLETA O FLUXO SEM MUDAR O CONCEITO
                                        onBack()
                                    }
                                )
                            }
                        }
                    }
                }

                // FAB
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    FloatingActionButton(
                        onClick = onAdicionarProduto,
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = Color.White,
                        modifier = Modifier
                            .padding(bottom = 80.dp, end = 24.dp)
                            .size(64.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Adicionar Produto")
                    }
                }

                // Dialog exclusão
                produtoParaExcluir?.let { produto ->
                    AlertDialog(
                        onDismissRequest = { produtoParaExcluir = null },
                        title = { Text("Excluir produto?") },
                        text = {
                            Text("${produto.emoji} ${produto.nome}")
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    produtoViewModel.remover(produto.id)
                                    produtoParaExcluir = null
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Text("Excluir")
                            }
                        },
                        dismissButton = {
                            OutlinedButton(onClick = { produtoParaExcluir = null }) {
                                Text("Cancelar")
                            }
                        }
                    )
                }
            }

            Footer()
        }
    }
}

@Composable
fun ProdutoCard(
    produto: Produto,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onAddToComanda: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAddToComanda() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(produto.nome, fontWeight = FontWeight.Bold)
                    Text("R$ ${"%.2f".format(produto.preco)}")
                }

                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onDeleteClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Delete, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Excluir Produto")
            }
        }
    }
}
