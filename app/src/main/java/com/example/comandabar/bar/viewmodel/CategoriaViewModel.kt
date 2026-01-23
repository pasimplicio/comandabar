package com.example.comandabar.bar.viewmodel

import androidx.lifecycle.ViewModel
import com.example.comandabar.bar.repository.CategoriaRepository
import kotlinx.coroutines.flow.StateFlow
import com.example.comandabar.bar.model.Categoria

class CategoriaViewModel : ViewModel() {

    val categorias: StateFlow<List<Categoria>> =
        CategoriaRepository.categorias

    fun adicionarCategoria(nome: String) {
        CategoriaRepository.adicionar(nome)
    }

    /**
     * @return true se removeu, false se está em uso
     */
    fun removerCategoria(id: String): Boolean {
        return CategoriaRepository.remover(id)
    }
}
