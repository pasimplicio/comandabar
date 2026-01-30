package com.example.comandabar.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.comandabar.bar.ui.menu.BarMenuScreen

import com.example.comandabar.ui.HomeScreen
import com.example.comandabar.ui.SplashScreen

import com.example.comandabar.ui.relatorios.RelatoriosScreen

import com.example.comandabar.bar.ui.categoria.CategoriaFormScreen
import com.example.comandabar.bar.ui.categoria.CategoriaListScreen

import com.example.comandabar.bar.ui.comanda.GerarComandaScreen
import com.example.comandabar.bar.ui.comanda.ComandaBarScreen
import com.example.comandabar.bar.ui.comanda.ListaComandasScreen
import com.example.comandabar.bar.ui.comanda.VincularClienteComandaScreen

import com.example.comandabar.bar.ui.produtos.ProdutoFormScreen
import com.example.comandabar.bar.ui.produtos.ProdutosBarScreen

import com.example.comandabar.bar.ui.qrcode.QrCodeScreen

import com.example.comandabar.cliente.ui.home.ClienteFormScreen
import com.example.comandabar.cliente.ui.home.ClienteHomeScreen
import com.example.comandabar.cliente.ui.comanda.ClienteComandaScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {

        composable("splash") {
            SplashScreen(navController = navController)
        }

        // HOME (Menu do Bar)
        composable("home") {
            HomeScreen(navController = navController)
        }

        // MENU BAR (se você usa essa tela separada)
        composable("bar_menu") {
            BarMenuScreen(navController = navController)
        }

        // CLIENTES
        composable("clientes") {
            ClienteHomeScreen(navController = navController)
        }

        composable("cliente_form") {
            ClienteFormScreen(
                navController = navController,
                clienteCodigo = null
            )
        }

        composable(
            route = "cliente_form/{clienteCodigo}",
            arguments = listOf(navArgument("clienteCodigo") { type = NavType.StringType })
        ) { backStackEntry ->
            val clienteCodigo = backStackEntry.arguments?.getString("clienteCodigo")
            ClienteFormScreen(
                navController = navController,
                clienteCodigo = clienteCodigo
            )
        }

        // CATEGORIAS  ✅ (isso evita crash ao clicar no menu)
        composable("categorias") {
            CategoriaListScreen(navController = navController)
        }

        composable("categoria_form") {
            CategoriaFormScreen(
                navController = navController,
                categoriaId = null
            )
        }

        composable(
            route = "categoria_form/{categoriaId}",
            arguments = listOf(navArgument("categoriaId") { type = NavType.StringType })
        ) { backStackEntry ->
            val categoriaId = backStackEntry.arguments?.getString("categoriaId")
            CategoriaFormScreen(
                navController = navController,
                categoriaId = categoriaId
            )
        }

        // PRODUTOS
        composable("produtos") {
            ProdutosBarScreen(
                onBack = { navController.popBackStack() },
                onAdicionarProduto = { navController.navigate("produto_form") },
                onEditarProduto = { produtoId ->
                    navController.navigate("produto_form/$produtoId")
                }
            )
        }

        composable("produto_form") {
            ProdutoFormScreen(
                produtoId = null,
                onSalvar = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "produto_form/{produtoId}",
            arguments = listOf(navArgument("produtoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val produtoId = backStackEntry.arguments?.getString("produtoId")
            ProdutoFormScreen(
                produtoId = produtoId,
                onSalvar = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        // COMANDAS
        composable("gerar_comanda") {
            VincularClienteComandaScreen(navController = navController)
        }

        composable("comanda_produtos") {
            GerarComandaScreen(navController = navController)
        }

        composable("lista_comandas") {
            ListaComandasScreen(navController = navController)
        }

        composable("comanda_bar") {
            ComandaBarScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        // RELATÓRIOS
        composable("relatorios") {
            RelatoriosScreen(navController = navController)
        }

        // QR CODE
        composable("qrcode") {
            QrCodeScreen(navController = navController)
        }

        // CLIENTE (Área do cliente)
        composable("cliente_comanda") {
            ClienteComandaScreen(navController = navController)
        }
    }
}
