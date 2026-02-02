package com.example.comandabar.shared.repository

import android.content.Context
import com.example.comandabar.bar.model.Categoria
import com.example.comandabar.bar.model.Produto
import com.example.comandabar.bar.repository.ProdutoRepository
import com.example.comandabar.cliente.viewmodel.Cliente
import com.example.comandabar.shared.model.Comanda
import com.example.comandabar.shared.model.ItemComanda
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.UUID

object ComandaRepository {
    private const val QR_PREFIX = "COMANDA:"
    private const val QR_FIELD_SEPARATOR = "|"
    private const val QR_ITEM_SEPARATOR = ";"
    private const val QR_ITEM_FIELD_SEPARATOR = ","
    private val comandaIdRegex =
        Regex("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}")
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

    fun produtoEmUso(produtoId: String): Boolean {
        return _comandas.value.any { comanda ->
            comanda.itens.any { item -> item.produto.id == produtoId }
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

    fun removerProduto(produtoId: String) {
        val comanda = _comandaAtiva.value ?: _comandaSelecionada.value ?: return
        val itens = comanda.itens.toMutableList()
        val index = itens.indexOfFirst { it.produto.id == produtoId }
        if (index >= 0) {
            itens.removeAt(index)
            atualizarComanda(comanda.copy(itens = itens))
        }
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
        return criarComandaFromQrPayload(payload)
    }

    private fun buildQrPayload(comanda: Comanda): String {
        val itensPayload = comanda.itens.joinToString(QR_ITEM_SEPARATOR) { item ->
            listOf(
                item.produto.id,
                item.quantidade.toString(),
                item.produto.preco.toString(),
                encodeField(item.produto.nome),
                encodeField(item.produto.emoji),
                encodeField(item.produto.categoria.id),
                encodeField(item.produto.categoria.nome)
            ).joinToString(QR_ITEM_FIELD_SEPARATOR)
        }
        return listOf(
            "$QR_PREFIX${comanda.id}",
            "C:${encodeField(comanda.cliente.codigo)}",
            "N:${encodeField(comanda.cliente.nome)}",
            "S:${comanda.status.name}",
            "I:$itensPayload"
        ).joinToString(QR_FIELD_SEPARATOR)
    }

    private fun parseQrPayload(qrCodeData: String): QrPayload? {
        val trimmed = qrCodeData.trim()
        val prefixIndex = trimmed.indexOf(QR_PREFIX)
        val candidateIdSource = when {
            prefixIndex >= 0 -> trimmed.substring(prefixIndex + QR_PREFIX.length).trim()
            else -> trimmed
        }
        val uuidMatch = comandaIdRegex.find(candidateIdSource) ?: comandaIdRegex.find(trimmed)
        val id = uuidMatch?.value ?: return null

        if (!trimmed.contains(QR_FIELD_SEPARATOR)) {
            return QrPayload(id = id)
        }

        val fields = trimmed.split(QR_FIELD_SEPARATOR)
        var clienteCodigo: String? = null
        var clienteNome: String? = null
        var status: Comanda.Status? = null
        var itens = emptyList<QrItemPayload>()

        fields.forEach { field ->
            when {
                field.startsWith("C:") -> clienteCodigo = decodeField(field.removePrefix("C:"))
                field.startsWith("N:") -> clienteNome = decodeField(field.removePrefix("N:"))
                field.startsWith("S:") -> status =
                    runCatching { Comanda.Status.valueOf(field.removePrefix("S:")) }.getOrNull()
                field.startsWith("I:") -> itens = parseItensPayload(field.removePrefix("I:"))
            }
        }

        return QrPayload(
            id = id,
            clienteCodigo = clienteCodigo,
            clienteNome = clienteNome,
            status = status,
            itens = itens
        )
    }

    private fun parseItensPayload(payload: String): List<QrItemPayload> {
        if (payload.isBlank()) return emptyList()
        return payload.split(QR_ITEM_SEPARATOR).mapNotNull { itemRaw ->
            val parts = itemRaw.split(QR_ITEM_FIELD_SEPARATOR)
            if (parts.size < 7) {
                null
            } else {
                QrItemPayload(
                    produtoId = parts[0],
                    quantidade = parts[1].toIntOrNull() ?: 1,
                    preco = parts[2].toDoubleOrNull(),
                    nome = decodeField(parts[3]),
                    emoji = decodeField(parts[4]),
                    categoriaId = decodeField(parts[5]),
                    categoriaNome = decodeField(parts[6])
                )
            }
        }
    }

    private fun criarComandaFromQrPayload(payload: QrPayload): Comanda {
        val cliente = Cliente(
            codigo = payload.clienteCodigo ?: "QR-${payload.id.take(8)}",
            nome = payload.clienteNome ?: "Cliente QR"
        )

        val itens = payload.itens.mapNotNull { item ->
            val produto = ProdutoRepository.getProdutoById(item.produtoId)
                ?: item.toProdutoFallback()
            produto?.let { ItemComanda(it, item.quantidade) }
        }

        val comanda = Comanda(
            id = payload.id,
            cliente = cliente,
            itens = itens,
            status = payload.status ?: Comanda.Status.ABERTA,
            criadaEm = System.currentTimeMillis(),
            fechadaEm = null,
            qrCodeData = ""
        )
        return comanda.copy(qrCodeData = buildQrPayload(comanda))
    }

    private fun QrItemPayload.toProdutoFallback(): Produto? {
        val nomeFallback = nome ?: return null
        val categoria = Categoria(
            id = categoriaId ?: UUID.randomUUID().toString(),
            nome = categoriaNome ?: "Categoria"
        )
        return Produto(
            id = produtoId,
            nome = nomeFallback,
            preco = preco ?: 0.0,
            emoji = emoji ?: "🍺",
            categoria = categoria
        )
    }

    private fun encodeField(value: String): String {
        return URLEncoder.encode(value, Charsets.UTF_8.name())
    }

    private fun decodeField(value: String): String {
        return URLDecoder.decode(value, Charsets.UTF_8.name())
    }

    private data class QrPayload(
        val id: String,
        val clienteCodigo: String? = null,
        val clienteNome: String? = null,
        val status: Comanda.Status? = null,
        val itens: List<QrItemPayload> = emptyList()
    )

    private data class QrItemPayload(
        val produtoId: String,
        val quantidade: Int,
        val preco: Double?,
        val nome: String?,
        val emoji: String?,
        val categoriaId: String?,
        val categoriaNome: String?
    )
}
