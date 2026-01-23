package com.example.comandabar.cliente.repository

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ClienteRepository {

    private const val PREFS_NAME = "clientes_prefs"
    private const val KEY_CLIENTES = "clientes"

    private var context: Context? = null

    private val _clientes = MutableStateFlow<List<com.example.comandabar.cliente.viewmodel.Cliente>>(emptyList())
    val clientes: StateFlow<List<com.example.comandabar.cliente.viewmodel.Cliente>> = _clientes.asStateFlow()

    private val _clienteSelecionado = MutableStateFlow<com.example.comandabar.cliente.viewmodel.Cliente?>(null)
    val clienteSelecionado: StateFlow<com.example.comandabar.cliente.viewmodel.Cliente?> = _clienteSelecionado.asStateFlow()

    fun init(appContext: Context) {
        context = appContext.applicationContext
        carregar()
    }

    private fun carregar() {
        val ctx = context ?: return
        val prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val set = prefs.getStringSet(KEY_CLIENTES, emptySet()) ?: emptySet()

        _clientes.value = set.mapNotNull {
            val parts = it.split("|")
            if (parts.size == 2) com.example.comandabar.cliente.viewmodel.Cliente(parts[0], parts[1]) else null
        }
    }

    private fun salvar() {
        val ctx = context ?: return
        val prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val set = _clientes.value.map { "${it.codigo}|${it.nome}" }.toSet()

        prefs.edit()
            .putStringSet(KEY_CLIENTES, set)
            .apply()
    }

    fun adicionar(cliente: com.example.comandabar.cliente.viewmodel.Cliente) {
        _clientes.value = _clientes.value + cliente
        salvar()
    }

    fun remover(codigo: String) {
        _clientes.value = _clientes.value.filterNot { it.codigo == codigo }
        salvar()
    }

    fun selecionar(cliente: com.example.comandabar.cliente.viewmodel.Cliente) {
        _clienteSelecionado.value = cliente
    }

    fun limparSelecao() {
        _clienteSelecionado.value = null
    }
}