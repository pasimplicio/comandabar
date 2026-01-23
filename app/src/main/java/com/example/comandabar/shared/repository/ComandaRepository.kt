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
        _comandas.value = ComandaDao.carregarComandas()
        _comandaAtiva.value = ComandaDao.carregarComandaAtiva()
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
        val qrCode = "COMANDA-$id-${cliente.codigo}"

        val comanda = Comanda(
            id = id,
            cliente = cliente,
            itens = emptyList(),
            status = Comanda.Status.ABERTA,
            criadaEm = System.currentTimeMillis(),
            fechadaEm = null,
            qrCodeData = qrCode
        )

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
        val comanda = _comandaAtiva.value ?: return

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
        _comandaAtiva.value = comanda
        _comandas.value = _comandas.value.map {
            if (it.id == comanda.id) comanda else it
        }
        _comandaSelecionada.value = comanda

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
        return _comandas.value.find { it.qrCodeData == qrCodeData }
    }
}
