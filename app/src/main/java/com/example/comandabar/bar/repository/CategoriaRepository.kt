package com.example.comandabar.bar.repository

import android.content.Context
import com.example.comandabar.bar.model.Categoria
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.lang.reflect.Type

object CategoriaRepository {

    private const val PREFS_NAME = "categorias_prefs"
    private const val KEY_CATEGORIAS = "categorias"

    private lateinit var context: Context
    private val gson = Gson()

    private val _categorias = MutableStateFlow<List<Categoria>>(emptyList())
    val categorias: StateFlow<List<Categoria>> = _categorias.asStateFlow()

    fun init(appContext: Context) {
        context = appContext.applicationContext
        carregar()
    }

    fun adicionar(nome: String) {
        val nomeNormalizado = nome.trim()
        if (nomeNormalizado.isBlank()) return

        // Evita duplicar por nome (case-insensitive)
        val jaExiste = _categorias.value.any { it.nome.equals(nomeNormalizado, ignoreCase = true) }
        if (jaExiste) return

        val categoria = Categoria(nome = nomeNormalizado)
        _categorias.value = _categorias.value + categoria
        salvar()
    }

    /**
     * @return true se removeu, false se está em uso
     */
    fun remover(id: String): Boolean {
        // Se algum produto usa essa categoria, não remove
        val emUso = ProdutoRepository.produtos.value.any { produto ->
            produto.categoria.id == id
        }
        if (emUso) return false

        _categorias.value = _categorias.value.filterNot { it.id == id }
        salvar()
        return true
    }

    fun getById(id: String): Categoria? {
        return _categorias.value.firstOrNull { it.id == id }
    }

    private fun carregar() {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_CATEGORIAS, null)

        if (json.isNullOrBlank()) {
            _categorias.value = emptyList()
            return
        }

        val type: Type = TypeToken.getParameterized(List::class.java, Categoria::class.java).type
        _categorias.value =
            runCatching { gson.fromJson<List<Categoria>>(json, type) ?: emptyList() }
                .getOrElse { emptyList() }
    }

    private fun salvar() {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_CATEGORIAS, gson.toJson(_categorias.value))
            .apply()
    }
}
