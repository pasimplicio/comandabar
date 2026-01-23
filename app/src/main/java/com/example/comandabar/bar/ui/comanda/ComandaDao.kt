package com.example.comandabar.shared.repository

import android.content.Context
import com.example.comandabar.bar.model.Categoria
import com.example.comandabar.bar.model.Produto
import com.example.comandabar.cliente.viewmodel.Cliente
import com.example.comandabar.shared.model.Comanda
import com.example.comandabar.shared.model.ItemComanda
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

object ComandaDao {

    private const val PREFS_NAME = "comandas_prefs"
    private const val KEY_COMANDAS = "comandas"
    private const val KEY_COMANDA_ATIVA = "comanda_ativa"
    private const val KEY_COMANDA_SELECIONADA = "comanda_selecionada"

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

    fun carregarComandas(): List<Comanda> {
        ensureInitialized()

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val comandasJson = prefs.getString(KEY_COMANDAS, "[]") ?: "[]"

        return try {
            val listType: Type = getComandaListType()
            val comandasDTO = gson.fromJson<List<ComandaDTO>>(comandasJson, listType) ?: emptyList()
            comandasDTO.map { it.toComanda() }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun getComandaListType(): Type {
        return TypeToken.getParameterized(List::class.java, ComandaDTO::class.java).type
    }

    fun salvarComandas(comandas: List<Comanda>) {
        ensureInitialized()

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val comandasJson = gson.toJson(comandas.map { it.toComandaDTO() })
        prefs.edit()
            .putString(KEY_COMANDAS, comandasJson)
            .apply()
    }

    fun salvarComandaAtiva(comanda: Comanda?) {
        ensureInitialized()

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val comandaJson = if (comanda != null) gson.toJson(comanda.toComandaDTO()) else null
        prefs.edit()
            .putString(KEY_COMANDA_ATIVA, comandaJson)
            .apply()
    }

    fun carregarComandaAtiva(): Comanda? {
        ensureInitialized()

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val comandaJson = prefs.getString(KEY_COMANDA_ATIVA, null) ?: return null

        return try {
            val comandaDTO = gson.fromJson(comandaJson, ComandaDTO::class.java)
            comandaDTO?.toComanda()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun salvarComandaSelecionada(comanda: Comanda?) {
        ensureInitialized()

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val comandaJson = if (comanda != null) gson.toJson(comanda.toComandaDTO()) else null
        prefs.edit()
            .putString(KEY_COMANDA_SELECIONADA, comandaJson)
            .apply()
    }

    fun carregarComandaSelecionada(): Comanda? {
        ensureInitialized()

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val comandaJson = prefs.getString(KEY_COMANDA_SELECIONADA, null) ?: return null

        return try {
            val comandaDTO = gson.fromJson(comandaJson, ComandaDTO::class.java)
            comandaDTO?.toComanda()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun ensureInitialized() {
        if (!isInitialized) {
            throw IllegalStateException("ComandaDao não foi inicializado. Chame init() primeiro.")
        }
    }

    private data class ComandaDTO(
        val id: String,
        val clienteCodigo: String,
        val clienteNome: String,
        val itens: List<ItemComandaDTO>,
        val status: String,
        val criadaEm: Long,
        val fechadaEm: Long? = null,
        val qrCodeData: String = ""
    )

    private data class ItemComandaDTO(
        val produtoId: String,
        val produtoNome: String,
        val produtoPreco: Double,
        val produtoEmoji: String,
        val produtoCategoriaId: String,
        val produtoCategoriaNome: String,
        val quantidade: Int = 1
    )

    private fun Comanda.toComandaDTO(): ComandaDTO {
        return ComandaDTO(
            id = id,
            clienteCodigo = cliente.codigo,
            clienteNome = cliente.nome,
            itens = itens.map { it.toItemComandaDTO() },
            status = status.name, // (Status continua enum, isso é OK)
            criadaEm = criadaEm,
            fechadaEm = fechadaEm,
            qrCodeData = qrCodeData
        )
    }

    private fun ComandaDTO.toComanda(): Comanda {
        val cliente = Cliente(clienteCodigo, clienteNome)

        val itensConvertidos = itens.map { dto ->
            val produto = Produto(
                id = dto.produtoId,
                nome = dto.produtoNome,
                preco = dto.produtoPreco,
                emoji = dto.produtoEmoji,
                categoria = Categoria(
                    id = dto.produtoCategoriaId,
                    nome = dto.produtoCategoriaNome
                )
            )
            ItemComanda(produto, dto.quantidade)
        }

        return Comanda(
            id = id,
            cliente = cliente,
            itens = itensConvertidos,
            status = Comanda.Status.valueOf(status), // (Status continua enum)
            criadaEm = criadaEm,
            fechadaEm = fechadaEm,
            qrCodeData = qrCodeData
        )
    }

    private fun ItemComanda.toItemComandaDTO(): ItemComandaDTO {
        return ItemComandaDTO(
            produtoId = produto.id,
            produtoNome = produto.nome,
            produtoPreco = produto.preco,
            produtoEmoji = produto.emoji,
            produtoCategoriaId = produto.categoria.id,
            produtoCategoriaNome = produto.categoria.nome,
            quantidade = quantidade
        )
    }
}
