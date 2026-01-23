package com.example.comandabar.bar.viewmodel

import androidx.lifecycle.ViewModel
import com.example.comandabar.bar.model.Produto
import com.example.comandabar.shared.repository.ComandaRepository
import kotlinx.coroutines.flow.StateFlow

class ComandaViewModel : ViewModel() {

    val comandaSelecionada: StateFlow<com.example.comandabar.shared.model.Comanda?>
        get() = ComandaRepository.comandaSelecionada

    val comandasAtivas: StateFlow<List<com.example.comandabar.shared.model.Comanda>>
        get() = ComandaRepository.comandasAtivas

    val comandaAtiva: StateFlow<com.example.comandabar.shared.model.Comanda?>
        get() = ComandaRepository.comandaAtiva

    fun verificarClienteTemComandaAberta(cliente: com.example.comandabar.cliente.viewmodel.Cliente): Boolean {
        return ComandaRepository.verificarClienteTemComandaAberta(cliente)
    }

    fun obterComandaAbertaDoCliente(cliente: com.example.comandabar.cliente.viewmodel.Cliente): com.example.comandabar.shared.model.Comanda? {
        return ComandaRepository.obterComandaAbertaDoCliente(cliente)
    }

    fun criarComanda(cliente: com.example.comandabar.cliente.viewmodel.Cliente): Result<String> {
        return ComandaRepository.criarComanda(cliente)
    }

    fun adicionarProduto(produto: Produto) {
        ComandaRepository.adicionarProduto(produto)
    }

    fun fecharComanda(comandaId: String) {
        ComandaRepository.fecharComanda(comandaId)
    }

    fun limparComandaAtiva() {
        ComandaRepository.limparComandaAtiva()
    }

    fun selecionarComanda(comandaId: String) {
        ComandaRepository.selecionarComanda(comandaId)
    }
}