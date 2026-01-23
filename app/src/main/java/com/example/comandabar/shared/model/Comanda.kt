package com.example.comandabar.shared.model

import com.example.comandabar.cliente.viewmodel.Cliente

data class Comanda(
    val id: String,
    val cliente: Cliente,
    val itens: List<ItemComanda>,
    val status: Status,
    val criadaEm: Long,
    val fechadaEm: Long? = null,
    val qrCodeData: String = ""
) {
    val total: Double
        get() = itens.sumOf { it.subtotal }

    enum class Status {
        ABERTA, FECHADA
    }

    fun isAberta(): Boolean = status == Status.ABERTA
}