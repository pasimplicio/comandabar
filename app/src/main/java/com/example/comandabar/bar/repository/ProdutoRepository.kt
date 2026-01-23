package com.example.comandabar.bar.repository

import android.content.Context
import com.example.comandabar.bar.model.Produto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ProdutoRepository {

    private val _produtos = MutableStateFlow(emptyList<Produto>())
    val produtos: StateFlow<List<Produto>> = _produtos.asStateFlow()

    fun init(context: Context) {
        ProdutoDao.init(context)
        carregarDadosPersistentes()
    }

    private fun carregarDadosPersistentes() {
        _produtos.value = ProdutoDao.carregarProdutos()
    }

    private fun salvarProdutos() {
        ProdutoDao.salvarProdutos(_produtos.value)
    }

    fun adicionar(produto: Produto) {
        _produtos.value = _produtos.value + produto
        salvarProdutos()
    }

    fun atualizar(produto: Produto) {
        _produtos.value = _produtos.value.map {
            if (it.id == produto.id) produto else it
        }
        salvarProdutos()
    }

    fun remover(id: String) {
        _produtos.value = _produtos.value.filterNot { it.id == id }
        salvarProdutos()
    }

    fun getProdutoById(id: String): Produto? {
        return _produtos.value.find { it.id == id }
    }
}