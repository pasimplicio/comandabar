package com.example.comandabar.shared.repository

import android.content.Context
import com.example.comandabar.bar.model.Produto
import com.example.comandabar.cliente.viewmodel.Cliente
import com.example.comandabar.shared.model.Comanda
import com.example.comandabar.shared.model.ItemComanda
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

object ComandaRepository {
    private val gson = Gson()

    private val _comandaAtiva = MutableStateFlow<Comanda?>(null)
    val comandaAtiva: StateFlow<Comanda?> = _comandaAtiva.asStateFlow()

    private val _comandas = MutableStateFlow<List<Comanda>>(emptyList())
    val comandas: StateFlow<List<Comanda>> = _comandas.asStateFlow()

    private val _comandasAtivas = MutableStateFlow<List<Comanda>>(emptyList())
    val comandasAtivas: StateFlow<List<Comanda>> = _comandasAtivas.asStateFlow()

    private val _comandaSelecionada = MutableStateFlow<Comanda?>(null)
    val comandaSelecionada: StateFlow<Comanda?> = _comandaSelecionada.asStateFlow()

    fun init(context: Context) {
        ComandaDao.init(context)
        carregarDadosPersistentes()
        atualizarComandasAtivas()
    }

    private fun carregarDadosPersistentes() {
        _comandas.value = ComandaDao.carregarComandas().map { comanda ->
            comanda.copy(qrCodeData = buildQrPayload(comanda))
        }
        _comandaAtiva.value = ComandaDao.carregarComandaAtiva()?.let { comanda ->
            comanda.copy(qrCodeData = buildQrPayload(comanda))
        }
        _comandaSelecionada.value = ComandaDao.carregarComandaSelecionada()?.let { comanda ->
            comanda.copy(qrCodeData = buildQrPayload(comanda))
        }
    }

    private fun salvarComandas() {
        ComandaDao.salvarComandas(_comandas.value)
    }

    private fun salvarComandaAtiva() {
        ComandaDao.salvarComandaAtiva(_comandaAtiva.value)
    }

    private fun salvarComandaSelecionada() {
        ComandaDao.salvarComandaSelecionada(_comandaSelecionada.value)
    }

    private fun atualizarComandasAtivas() {
        _comandasAtivas.value = _comandas.value.filter {
            it.status == Comanda.Status.ABERTA
        }
    }

    fun verificarClienteTemComandaAberta(cliente: Cliente): Boolean {
        return _comandas.value.any {
            it.cliente.codigo == cliente.codigo &&
                    it.status == Comanda.Status.ABERTA
        }
    }

    fun obterComandaAbertaDoCliente(cliente: Cliente): Comanda? {
        return _comandas.value.find {
            it.cliente.codigo == cliente.codigo &&
                    it.status == Comanda.Status.ABERTA
        }
    }

    fun criarComanda(cliente: Cliente): Result<String> {
        if (verificarClienteTemComandaAberta(cliente)) {
            val existente = obterComandaAbertaDoCliente(cliente)
            return Result.failure(
                Exception("Cliente já tem comanda aberta: ${existente?.id}")
            )
        }

        val id = UUID.randomUUID().toString()

        val baseComanda = Comanda(
            id = id,
            cliente = cliente,
            itens = emptyList(),
            status = Comanda.Status.ABERTA,
            criadaEm = System.currentTimeMillis(),
            fechadaEm = null,
            qrCodeData = ""
        )
        val comanda = baseComanda.copy(qrCodeData = buildQrPayload(baseComanda))

        _comandas.value = _comandas.value + comanda
        _comandaAtiva.value = comanda
        _comandaSelecionada.value = comanda

        salvarComandas()
        salvarComandaAtiva()
        salvarComandaSelecionada()
        atualizarComandasAtivas()

        return Result.success(id)
    }

    fun adicionarProduto(produto: Produto) {
        val comanda = _comandaAtiva.value ?: _comandaSelecionada.value ?: return
        if (_comandaAtiva.value == null) {
            _comandaAtiva.value = comanda
        }

        val itens = comanda.itens.toMutableList()
        val index = itens.indexOfFirst { it.produto.id == produto.id }

        if (index >= 0) {
            val item = itens[index]
            itens[index] = item.copy(quantidade = item.quantidade + 1)
        } else {
            itens.add(ItemComanda(produto))
        }

        atualizarComanda(comanda.copy(itens = itens))
    }

    private fun atualizarComanda(comanda: Comanda) {
        val updatedComanda = comanda.copy(qrCodeData = buildQrPayload(comanda))
        _comandaAtiva.value = updatedComanda
        _comandas.value = _comandas.value.map {
            if (it.id == updatedComanda.id) updatedComanda else it
        }
        _comandaSelecionada.value = updatedComanda

        salvarComandas()
        salvarComandaAtiva()
        salvarComandaSelecionada()
        atualizarComandasAtivas()
    }

    fun limparComandaAtiva() {
        _comandaAtiva.value = null
        salvarComandaAtiva()
    }

    fun selecionarComanda(id: String) {
        _comandaSelecionada.value =
            _comandas.value.find { it.id == id }
        salvarComandaSelecionada()
    }

    fun fecharComanda(id: String) {
        _comandas.value = _comandas.value.map {
            if (it.id == id)
                it.copy(
                    status = Comanda.Status.FECHADA,
                    fechadaEm = System.currentTimeMillis()
                )
            else it
        }

        _comandaAtiva.value = null
        salvarComandas()
        salvarComandaAtiva()
        atualizarComandasAtivas()
    }

    fun getComandaPorQrCode(qrCodeData: String): Comanda? {
        val normalized = qrCodeData.trim()
        if (normalized.isEmpty()) {
            return null
        }
        val directMatch = _comandas.value.find {
            val stored = it.qrCodeData.trim()
            stored == normalized || stored.contains(normalized) || normalized.contains(stored)
        }
        if (directMatch != null) {
            return directMatch
        }

        val payload = parseQrPayload(normalized) ?: return null
        val existing = _comandas.value.find { it.id == payload.id }
        if (existing != null) {
            return existing
        }

        val comandaFromQr = payload.toComanda()
        _comandas.value = _comandas.value + comandaFromQr
        _comandaSelecionada.value = comandaFromQr
        salvarComandas()
        salvarComandaSelecionada()
        atualizarComandasAtivas()
        return comandaFromQr
    }

    private fun buildQrPayload(comanda: Comanda): String {
        val payload = QrComandaPayload(
            id = comanda.id,
            clienteCodigo = comanda.cliente.codigo,
            clienteNome = comanda.cliente.nome,
            itens = comanda.itens.map { item ->
                QrComandaItem(
                    produtoId = item.produto.id,
                    produtoNome = item.produto.nome,
                    produtoPreco = item.produto.preco,
                    produtoEmoji = item.produto.emoji,
                    produtoCategoriaId = item.produto.categoria.id,
                    produtoCategoriaNome = item.produto.categoria.nome,
                    quantidade = item.quantidade
                )
            },
            status = comanda.status.name,
            criadaEm = comanda.criadaEm,
            fechadaEm = comanda.fechadaEm
        )
        return gson.toJson(payload)
    }

    private fun parseQrPayload(qrCodeData: String): QrComandaPayload? {
        return try {
            gson.fromJson(qrCodeData, QrComandaPayload::class.java)
        } catch (e: Exception) {
            null
        }
    }

    private data class QrComandaPayload(
        val id: String,
        val clienteCodigo: String,
        val clienteNome: String,
        val itens: List<QrComandaItem>,
        val status: String,
        val criadaEm: Long,
        val fechadaEm: Long?
    ) {
        fun toComanda(): Comanda {
            val cliente = Cliente(clienteCodigo, clienteNome)
            val itensConvertidos = itens.map { item ->
                val produto = Produto(
                    id = item.produtoId,
                    nome = item.produtoNome,
                    preco = item.produtoPreco,
                    emoji = item.produtoEmoji,
                    categoria = com.example.comandabar.bar.model.Categoria(
                        id = item.produtoCategoriaId,
                        nome = item.produtoCategoriaNome
                    )
                )
                ItemComanda(produto, item.quantidade)
            }
            return Comanda(
                id = id,
                cliente = cliente,
                itens = itensConvertidos,
                status = Comanda.Status.valueOf(status),
                criadaEm = criadaEm,
                fechadaEm = fechadaEm,
                qrCodeData = gson.toJson(this)
            )
        }
    }

    private data class QrComandaItem(
        val produtoId: String,
        val produtoNome: String,
        val produtoPreco: Double,
        val produtoEmoji: String,
        val produtoCategoriaId: String,
        val produtoCategoriaNome: String,
        val quantidade: Int
    )
}
