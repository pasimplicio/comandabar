package com.example.comandabar.bar.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.comandabar.bar.model.Categoria

@Composable
fun CategoriaDropdown(
    categorias: List<Categoria>,
    categoriaSelecionadaId: String?,
    onCategoriaSelecionada: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val categoriaSelecionada =
        categorias.find { it.id == categoriaSelecionadaId }?.nome ?: "Selecione a categoria"

    OutlinedTextField(
        value = categoriaSelecionada,
        onValueChange = {},
        readOnly = true,
        label = { Text("Categoria") },
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = true }
    )

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        categorias.forEach { categoria ->
            DropdownMenuItem(
                text = { Text(categoria.nome) },
                onClick = {
                    onCategoriaSelecionada(categoria.id)
                    expanded = false
                }
            )
        }
    }
}
