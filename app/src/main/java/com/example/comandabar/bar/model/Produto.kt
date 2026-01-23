package com.example.comandabar.bar.model

import java.util.UUID

data class Produto(
    val id: String = UUID.randomUUID().toString(),
    val nome: String,
    val preco: Double,
    val emoji: String,
    val categoria: Categoria
)
