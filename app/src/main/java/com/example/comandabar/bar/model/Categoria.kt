package com.example.comandabar.bar.model

import java.util.UUID

data class Categoria(
    val id: String = UUID.randomUUID().toString(),
    val nome: String
)
