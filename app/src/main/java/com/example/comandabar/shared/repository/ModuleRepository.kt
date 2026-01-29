package com.example.comandabar.shared.repository

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppModule {
    CLIENTE,
    BAR
}

object ModuleRepository {
    private const val PREFS_NAME = "module_prefs"
    private const val KEY_SELECTED_MODULE = "selected_module"

    @Volatile
    private var isInitialized = false
    private lateinit var context: Context

    private val _selectedModule = MutableStateFlow<AppModule?>(null)
    val selectedModule: StateFlow<AppModule?> = _selectedModule.asStateFlow()

    fun init(appContext: Context) {
        if (!isInitialized) {
            synchronized(this) {
                if (!isInitialized) {
                    context = appContext.applicationContext
                    carregarModuloSelecionado()
                    isInitialized = true
                }
            }
        }
    }

    private fun carregarModuloSelecionado() {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val moduleName = prefs.getString(KEY_SELECTED_MODULE, null)
        _selectedModule.value = moduleName?.let { name ->
            runCatching { AppModule.valueOf(name) }.getOrNull()
        }
    }

    fun selecionarModulo(modulo: AppModule) {
        _selectedModule.value = modulo
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_SELECTED_MODULE, modulo.name)
            .apply()
    }

    fun limparSelecao() {
        _selectedModule.value = null
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .remove(KEY_SELECTED_MODULE)
            .apply()
    }
}
