package com.example.comandabar.bar.ui.produtos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.comandabar.bar.model.Categoria
import com.example.comandabar.bar.repository.CategoriaRepository
import com.example.comandabar.bar.viewmodel.ProdutoViewModel

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProdutoFormScreen(
    produtoId: String? = null,
    onSalvar: () -> Unit,
    onBack: () -> Unit,
    produtoViewModel: ProdutoViewModel = viewModel()
) {
    val produto = produtoId?.let { produtoViewModel.getProdutoById(it) }

    val categorias by CategoriaRepository.categorias.collectAsState()

    var nome by remember { mutableStateOf(produto?.nome ?: "") }
    var preco by remember { mutableStateOf(produto?.preco?.toString() ?: "") }
    var emoji by remember { mutableStateOf(produto?.emoji ?: "🍺") }

    // Categoria selecionada: começa com a do produto (se editando) ou null (se novo)
    var categoriaSelecionada by remember {
        mutableStateOf<Categoria?>(produto?.categoria)
    }

    var nomeError by remember { mutableStateOf(false) }
    var precoError by remember { mutableStateOf(false) }

    val precoValido = preco.toDoubleOrNull()
    val isFormValid =
        nome.isNotBlank() && precoValido != null && categoriaSelecionada != null

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // TopAppBar personalizada
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
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Voltar",
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Text(
                                text = if (produtoId == null) "Novo Produto" else "Editar Produto",
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

            // Conteúdo principal com Scroll
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {

                    // Preview
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Visualização do Produto",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )

                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(MaterialTheme.colorScheme.secondaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = emoji,
                                    style = MaterialTheme.typography.displayMedium
                                )
                            }

                            Text(
                                text = nome.ifEmpty { "Nome do Produto" },
                                style = MaterialTheme.typography.titleMedium,
                                color = if (nome.isNotBlank())
                                    MaterialTheme.colorScheme.onSurface
                                else
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            )

                            Text(
                                text = if (precoValido != null)
                                    "R$ ${"%.2f".format(precoValido)}"
                                else "R$ 0,00",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }

                    // Formulário
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {

                            OutlinedTextField(
                                value = nome,
                                onValueChange = {
                                    nome = it
                                    nomeError = it.isBlank()
                                },
                                label = { Text("Nome do produto") },
                                isError = nomeError,
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                leadingIcon = {
                                    Icon(Icons.Default.LocalBar, contentDescription = null)
                                }
                            )

                            if (nomeError) {
                                Text(
                                    text = "O nome do produto é obrigatório",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }

                            OutlinedTextField(
                                value = preco,
                                onValueChange = {
                                    preco = it
                                    precoError = it.toDoubleOrNull() == null
                                },
                                label = { Text("Preço (R$)") },
                                isError = precoError,
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                leadingIcon = {
                                    Icon(Icons.Default.AttachMoney, contentDescription = null)
                                }
                            )

                            if (precoError) {
                                Text(
                                    text = "Digite um preço válido",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }

                            OutlinedTextField(
                                value = emoji,
                                onValueChange = { emoji = it },
                                label = { Text("Emoji") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                leadingIcon = {
                                    Icon(Icons.Default.EmojiEmotions, contentDescription = null)
                                }
                            )

                            // Categoria
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = "Categoria",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )

                                if (categorias.isEmpty()) {
                                    Text(
                                        text = "Nenhuma categoria cadastrada. Cadastre em Gerenciar Categorias.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                } else {
                                    FlowRow(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        categorias.forEach { cat ->
                                            FilterChip(
                                                selected = categoriaSelecionada?.id == cat.id,
                                                onClick = { categoriaSelecionada = cat },
                                                label = { Text(cat.nome) }
                                            )
                                        }
                                    }
                                }

                                if (categoriaSelecionada == null) {
                                    Text(
                                        text = "Selecione uma categoria",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botão de salvar
                    Button(
                        onClick = {
                            val valor = precoValido ?: return@Button
                            val cat = categoriaSelecionada ?: return@Button

                            if (produtoId == null) {
                                produtoViewModel.adicionarProduto(
                                    nome = nome,
                                    preco = valor,
                                    emoji = emoji,
                                    categoria = cat
                                )
                            } else {
                                produtoViewModel.atualizarProduto(
                                    id = produtoId,
                                    nome = nome,
                                    preco = valor,
                                    emoji = emoji,
                                    categoria = cat
                                )
                            }
                            onSalvar()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = isFormValid
                    ) {
                        Icon(
                            imageVector = if (produtoId == null)
                                Icons.Default.Add else Icons.Default.Save,
                            contentDescription = null
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = if (produtoId == null)
                                "Adicionar Produto"
                            else
                                "Salvar Alterações"
                        )
                    }

                    if (!isFormValid) {
                        Text(
                            text = "Preencha todos os campos corretamente",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(vertical = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}
