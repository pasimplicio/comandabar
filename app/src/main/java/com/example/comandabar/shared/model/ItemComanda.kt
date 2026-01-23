package com.example.comandabar.shared.model

import com.example.comandabar.bar.model.Produto

data class ItemComanda(
    val produto: Produto,
    val quantidade: Int = 1
) {

    val subtotal: Double
        get() = produto.preco * quantidade
}
