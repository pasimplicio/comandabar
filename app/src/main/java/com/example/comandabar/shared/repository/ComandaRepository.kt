package com.example.comandabar.shared.repository

import android.content.Context
import com.example.comandabar.bar.model.Produto
import com.example.comandabar.cliente.viewmodel.Cliente
import com.example.comandabar.shared.model.Comanda
import com.example.comandabar.shared.model.ItemComanda
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

object ComandaRepository {

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
        // Carregar comandas salvas
        _comandas.value = ComandaDao.carregarComandas()

        // Carregar comanda ativa salva
        _comandaAtiva.value = ComandaDao.carregarComandaAtiva()

        // Carregar comanda selecionada salva
        _comandaSelecionada.value = ComandaDao.carregarComandaSelecionada()
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
        _comandasAtivas.value = _comandas.value.filter { it.status == Comanda.Status.ABERTA }
    }

    fun verificarClienteTemComandaAberta(cliente: Cliente): Boolean {
        return _comandas.value.any {
            it.cliente.codigo == cliente.codigo && it.status == Comanda.Status.ABERTA
        }
    }

    fun obterComandaAbertaDoCliente(cliente: Cliente): Comanda? {
        return _comandas.value.find {
            it.cliente.codigo == cliente.codigo && it.status == Comanda.Status.ABERTA
        }
    }

    fun criarComanda(cliente: Cliente): Result<String> {
        // Verificar se o cliente já tem comanda aberta
        if (verificarClienteTemComandaAberta(cliente)) {
            val comandaExistente = obterComandaAbertaDoCliente(cliente)
            return Result.failure(Exception(
                "Cliente já tem uma comanda aberta! ID: ${comandaExistente?.id?.take(8)}..."
            ))
        }

        val id = UUID.randomUUID().toString()
        val qrCodeData = "COMANDA-$id-${cliente.codigo}"

        val comanda = Comanda(
            id = id,
            cliente = cliente,
            itens = emptyList(),
            status = Comanda.Status.ABERTA,
            criadaEm = System.currentTimeMillis(),
            fechadaEm = null,
            qrCodeData = qrCodeData
        )

        _comandaAtiva.value = comanda
        _comandas.value = _comandas.value + comanda
        _comandaSelecionada.value = comanda

        // Persistir dados
        salvarComandas()
        salvarComandaAtiva()
        salvarComandaSelecionada()

        atualizarComandasAtivas()

        return Result.success(id)
    }

    fun adicionarProduto(produto: Produto) {
        val comanda = _comandaAtiva.value ?: return

        val itensAtualizados = comanda.itens.toMutableList()
        val index = itensAtualizados.indexOfFirst { it.produto.id == produto.id }

        if (index >= 0) {
            val item = itensAtualizados[index]
            itensAtualizados[index] =
                item.copy(quantidade = item.quantidade + 1)
        } else {
            itensAtualizados.add(
                ItemComanda(
                    produto = produto,
                    quantidade = 1
                )
            )
        }

        atualizarComanda(comanda.copy(itens = itensAtualizados))
    }

    private fun atualizarComanda(comanda: Comanda) {
        _comandaAtiva.value = comanda
        _comandas.value = _comandas.value.map {
            if (it.id == comanda.id) comanda else it
        }
        if (_comandaSelecionada.value?.id == comanda.id) {
            _comandaSelecionada.value = comanda
        }

        // Persistir dados
        salvarComandas()
        salvarComandaAtiva()
        salvarComandaSelecionada()

        atualizarComandasAtivas()
    }

    fun limparComandaAtiva() {
        _comandaAtiva.value = null
        salvarComandaAtiva()
    }

    fun selecionarComanda(comandaId: String) {
        val comanda = _comandas.value.find { it.id == comandaId }
        _comandaSelecionada.value = comanda
        salvarComandaSelecionada()
    }

    fun fecharComanda(comandaId: String) {
        _comandas.value = _comandas.value.map {
            if (it.id == comandaId) {
                val comandaFechada = it.copy(
                    status = Comanda.Status.FECHADA,
                    fechadaEm = System.currentTimeMillis()
                )
                if (_comandaSelecionada.value?.id == comandaId) {
                    _comandaSelecionada.value = comandaFechada
                }
                if (_comandaAtiva.value?.id == comandaId) {
                    _comandaAtiva.value = null
                }
                comandaFechada
            } else it
        }

        // Persistir dados
        salvarComandas()
        salvarComandaAtiva()
        salvarComandaSelecionada()

        atualizarComandasAtivas()
    }

    fun getComandasFechadas(): List<Comanda> {
        return _comandas.value.filter { it.status == Comanda.Status.FECHADA }
    }

    fun getComandaPorId(id: String): Comanda? {
        return _comandas.value.find { it.id == id }
    }

    fun getComandaPorQrCode(qrCodeData: String): Comanda? {
        return _comandas.value.find { it.qrCodeData == qrCodeData }
    }

    // Método para limpar todas as comandas (apenas para desenvolvimento)
    fun limparTodasComandas() {
        _comandas.value = emptyList()
        _comandaAtiva.value = null
        _comandaSelecionada.value = null

        // Persistir
        salvarComandas()
        salvarComandaAtiva()
        salvarComandaSelecionada()

        atualizarComandasAtivas()
    }
}