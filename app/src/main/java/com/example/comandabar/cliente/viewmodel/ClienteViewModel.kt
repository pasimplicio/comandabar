package com.example.comandabar.cliente.viewmodel

import androidx.lifecycle.ViewModel
import com.example.comandabar.cliente.repository.ClienteRepository
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

class ClienteViewModel : ViewModel() {

    val clientes: StateFlow<List<Cliente>> = ClienteRepository.clientes

    // Método para obter o cliente selecionado
    fun clienteSelecionado(): StateFlow<Cliente?> {
        return ClienteRepository.clienteSelecionado
    }

    fun adicionarCliente(nome: String) {
        ClienteRepository.adicionar(
            Cliente(
                codigo = UUID.randomUUID().toString(),
                nome = nome
            )
        )
    }

    fun removerCliente(codigo: String) {
        ClienteRepository.remover(codigo)
    }
}