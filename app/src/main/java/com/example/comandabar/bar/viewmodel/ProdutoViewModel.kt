package com.example.comandabar.bar.viewmodel

import androidx.lifecycle.ViewModel
import com.example.comandabar.bar.model.Categoria
import com.example.comandabar.bar.model.Produto
import com.example.comandabar.bar.repository.ProdutoRepository
import com.example.comandabar.shared.repository.ComandaRepository
import kotlinx.coroutines.flow.StateFlow

class ProdutoViewModel : ViewModel() {

    val produtos: StateFlow<List<Produto>> = ProdutoRepository.produtos

    fun adicionarProduto(nome: String, preco: Double, emoji: String, categoria: Categoria) {
        val produto = Produto(
            nome = nome,
            preco = preco,
            emoji = emoji,
            categoria = categoria
        )
        ProdutoRepository.adicionar(produto)
    }

    fun atualizarProduto(id: String, nome: String, preco: Double, emoji: String, categoria: Categoria) {
        val produto = Produto(
            id = id,
            nome = nome,
            preco = preco,
            emoji = emoji,
            categoria = categoria
        )
        ProdutoRepository.atualizar(produto)
    }

    fun remover(id: String): Boolean {
        if (ComandaRepository.produtoEmUso(id)) {
            return false
        }
        ProdutoRepository.remover(id)
        return true
    }

    fun getProdutoById(id: String): Produto? {
        return ProdutoRepository.getProdutoById(id)
    }
}
