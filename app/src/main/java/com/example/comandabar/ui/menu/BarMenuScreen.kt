package com.example.comandabar.ui.menu

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.comandabar.ui.components.Footer
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun BarMenuScreen(navController: NavController) {

    var isContentVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        isContentVisible = true
    }

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
                                        text = "Menu do Bar",
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

                    // Conteúdo do menu
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Dados dos itens do menu
                        val menuItems = listOf(
                            MenuItemData(
                                icon = Icons.Default.Person,
                                title = "Gerenciar Clientes",
                                description = "Cadastrar e editar clientes",
                                route = "clientes",
                                color = MaterialTheme.colorScheme.primary
                            ),
                            MenuItemData(
                                icon = Icons.Default.LocalBar,
                                title = "Gerenciar Produtos",
                                description = "Adicionar e editar produtos",
                                route = "produtos",
                                color = MaterialTheme.colorScheme.secondary
                            ),
                            MenuItemData(
                                icon = Icons.Default.Receipt,
                                title = "Gerar Comanda",
                                description = "Criar nova comanda para cliente",
                                route = "gerar_comanda",
                                color = MaterialTheme.colorScheme.tertiary
                            ),
                            MenuItemData(
                                icon = Icons.AutoMirrored.Filled.ListAlt,
                                title = "Ver Comandas Ativas",
                                description = "Visualizar e gerenciar comandas",
                                route = "lista_comandas",
                                color = MaterialTheme.colorScheme.primary
                            ),
                            MenuItemData(
                                icon = Icons.Default.Assessment,
                                title = "Relatórios",
                                description = "Visualizar vendas e histórico",
                                route = "relatorios",
                                color = MaterialTheme.colorScheme.secondary
                            )
                        )

                        // Renderizar cada item com animação simples
                        menuItems.forEachIndexed { index, item ->
                            AnimatedVisibility(
                                visible = isContentVisible,
                                enter = fadeIn(
                                    animationSpec = tween(
                                        durationMillis = 300,
                                        delayMillis = 100 + (index * 80)
                                    )
                                ) + slideInVertically(
                                    animationSpec = tween(
                                        durationMillis = 400,
                                        delayMillis = 100 + (index * 80)
                                    ),
                                    initialOffsetY = { 30 }
                                )
                            ) {
                                MenuItem(
                                    icon = item.icon,
                                    title = item.title,
                                    description = item.description,
                                    onClick = {
                                        navController.navigate(item.route)
                                    },
                                    color = item.color
                                )
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

// Data class para os itens do menu
private data class MenuItemData(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val route: String,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuItem(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit,
    color: Color
) {
    var isHovered by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = if (isHovered) 1.02f else 1f
                scaleY = if (isHovered) 1.02f else 1f
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isHovered) 6.dp else 4.dp
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Aqui estava o erro - corrigido
                Surface(
                    color = color.copy(alpha = 0.1f),
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(12.dp)
                ) {}
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(28.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Acessar",
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}