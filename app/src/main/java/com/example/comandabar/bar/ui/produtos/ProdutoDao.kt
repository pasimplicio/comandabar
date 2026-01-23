package com.example.comandabar.bar.repository

import android.content.Context
import com.example.comandabar.bar.model.Categoria
import com.example.comandabar.bar.model.Produto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

object ProdutoDao {

    private const val PREFS_NAME = "produtos_prefs"
    private const val KEY_PRODUTOS = "produtos"

    @Volatile
    private var isInitialized = false
    private lateinit var context: Context
    private val gson = Gson()

    fun init(appContext: Context) {
        if (!isInitialized) {
            synchronized(this) {
                if (!isInitialized) {
                    context = appContext.applicationContext
                    isInitialized = true
                }
            }
        }
    }

    fun carregarProdutos(): List<Produto> {
        ensureInitialized()

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val produtosJson = prefs.getString(KEY_PRODUTOS, "[]") ?: "[]"

        return try {
            val listType: Type = getProdutoListType()
            val produtosDTO = gson.fromJson<List<ProdutoDTO>>(produtosJson, listType) ?: emptyList()
            produtosDTO.map { it.toProduto() }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun getProdutoListType(): Type {
        return TypeToken.getParameterized(List::class.java, ProdutoDTO::class.java).type
    }

    fun salvarProdutos(produtos: List<Produto>) {
        ensureInitialized()

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val produtosJson = gson.toJson(produtos.map { it.toProdutoDTO() })
        prefs.edit()
            .putString(KEY_PRODUTOS, produtosJson)
            .apply()
    }

    private fun ensureInitialized() {
        if (!isInitialized) {
            throw IllegalStateException("ProdutoDao não foi inicializado. Chame init() primeiro.")
        }
    }

    // DTO agora salva categoria como OBJETO (id + nome), não enum
    private data class ProdutoDTO(
        val id: String,
        val nome: String,
        val preco: Double,
        val emoji: String,
        val categoriaId: String,
        val categoriaNome: String
    )

    private fun Produto.toProdutoDTO(): ProdutoDTO {
        return ProdutoDTO(
            id = id,
            nome = nome,
            preco = preco,
            emoji = emoji,
            categoriaId = categoria.id,
            categoriaNome = categoria.nome
        )
    }

    private fun ProdutoDTO.toProduto(): Produto {
        return Produto(
            id = id,
            nome = nome,
            preco = preco,
            emoji = emoji,
            categoria = Categoria(
                id = categoriaId,
                nome = categoriaNome
            )
        )
    }
}
