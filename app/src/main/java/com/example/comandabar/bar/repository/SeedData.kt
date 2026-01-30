package com.example.comandabar.bar.repository

import com.example.comandabar.bar.model.Categoria
import com.example.comandabar.bar.model.Produto

object SeedData {
    private const val CATEGORIA_CERVEJAS_ID = "categoria_cervejas"
    private const val CATEGORIA_DESTILADOS_ID = "categoria_destilados"
    private const val CATEGORIA_VINHOS_ID = "categoria_vinhos"
    private const val CATEGORIA_REFRIGERANTES_ID = "categoria_refrigerantes"
    private const val CATEGORIA_AGUA_ID = "categoria_agua"
    private const val CATEGORIA_PETISCOS_ID = "categoria_petiscos"
    private const val CATEGORIA_DOCES_ID = "categoria_doces"

    val categorias: List<Categoria> = listOf(
        Categoria(id = CATEGORIA_CERVEJAS_ID, nome = "Cervejas"),
        Categoria(id = CATEGORIA_DESTILADOS_ID, nome = "Destilados"),
        Categoria(id = CATEGORIA_VINHOS_ID, nome = "Vinhos"),
        Categoria(id = CATEGORIA_REFRIGERANTES_ID, nome = "Refrigerantes"),
        Categoria(id = CATEGORIA_AGUA_ID, nome = "Água"),
        Categoria(id = CATEGORIA_PETISCOS_ID, nome = "Petiscos"),
        Categoria(id = CATEGORIA_DOCES_ID, nome = "Doces")
    )

    val produtos: List<Produto> = listOf(
        Produto(
            nome = "AMSTEL 350ml",
            preco = 5.5,
            emoji = "🍺",
            categoria = Categoria(id = CATEGORIA_CERVEJAS_ID, nome = "Cervejas")
        ),
        Produto(
            nome = "AMSTEL 600ml",
            preco = 11.0,
            emoji = "🍺",
            categoria = Categoria(id = CATEGORIA_CERVEJAS_ID, nome = "Cervejas")
        ),
        Produto(
            nome = "ANTARCTICA 350ml (LATA)",
            preco = 4.5,
            emoji = "🍺",
            categoria = Categoria(id = CATEGORIA_CERVEJAS_ID, nome = "Cervejas")
        ),
        Produto(
            nome = "ANTARCTICA 600ml",
            preco = 9.5,
            emoji = "🍺",
            categoria = Categoria(id = CATEGORIA_CERVEJAS_ID, nome = "Cervejas")
        ),
        Produto(
            nome = "BRAHMA 350ml",
            preco = 4.8,
            emoji = "🍺",
            categoria = Categoria(id = CATEGORIA_CERVEJAS_ID, nome = "Cervejas")
        ),
        Produto(
            nome = "BRAHMA (BUCHUDINHA)",
            preco = 4.0,
            emoji = "🍺",
            categoria = Categoria(id = CATEGORIA_CERVEJAS_ID, nome = "Cervejas")
        ),
        Produto(
            nome = "BRAHMA 600ml",
            preco = 10.0,
            emoji = "🍺",
            categoria = Categoria(id = CATEGORIA_CERVEJAS_ID, nome = "Cervejas")
        ),
        Produto(
            nome = "BUDWEISER 350ml",
            preco = 6.5,
            emoji = "🍺",
            categoria = Categoria(id = CATEGORIA_CERVEJAS_ID, nome = "Cervejas")
        ),
        Produto(
            nome = "BUDWEISER 600ml",
            preco = 12.5,
            emoji = "🍺",
            categoria = Categoria(id = CATEGORIA_CERVEJAS_ID, nome = "Cervejas")
        ),
        Produto(
            nome = "DEVASSA 350ml (LATA)",
            preco = 5.5,
            emoji = "🍺",
            categoria = Categoria(id = CATEGORIA_CERVEJAS_ID, nome = "Cervejas")
        ),
        Produto(
            nome = "DEVASSA 600ml",
            preco = 11.5,
            emoji = "🍺",
            categoria = Categoria(id = CATEGORIA_CERVEJAS_ID, nome = "Cervejas")
        ),
        Produto(
            nome = "GLACIAL 350ml (LATA)",
            preco = 4.0,
            emoji = "🍺",
            categoria = Categoria(id = CATEGORIA_CERVEJAS_ID, nome = "Cervejas")
        ),
        Produto(
            nome = "GLACIAL 600ml",
            preco = 9.0,
            emoji = "🍺",
            categoria = Categoria(id = CATEGORIA_CERVEJAS_ID, nome = "Cervejas")
        ),
        Produto(
            nome = "HEINEKEN 350ml",
            preco = 7.5,
            emoji = "🍺",
            categoria = Categoria(id = CATEGORIA_CERVEJAS_ID, nome = "Cervejas")
        ),
        Produto(
            nome = "HEINEKEN 600ml",
            preco = 13.5,
            emoji = "🍺",
            categoria = Categoria(id = CATEGORIA_CERVEJAS_ID, nome = "Cervejas")
        ),
        Produto(
            nome = "IMPERIO (BUCHUDINHA)",
            preco = 4.0,
            emoji = "🍺",
            categoria = Categoria(id = CATEGORIA_CERVEJAS_ID, nome = "Cervejas")
        ),
        Produto(
            nome = "ITAIPAVA 350ml (LATA)",
            preco = 4.2,
            emoji = "🍺",
            categoria = Categoria(id = CATEGORIA_CERVEJAS_ID, nome = "Cervejas")
        ),
        Produto(
            nome = "ITAIPAVA 600ml",
            preco = 9.0,
            emoji = "🍺",
            categoria = Categoria(id = CATEGORIA_CERVEJAS_ID, nome = "Cervejas")
        ),
        Produto(
            nome = "SKOL 350ml (LATA)",
            preco = 4.5,
            emoji = "🍺",
            categoria = Categoria(id = CATEGORIA_CERVEJAS_ID, nome = "Cervejas")
        ),
        Produto(
            nome = "SKOL (BUCHUDINHA)",
            preco = 4.0,
            emoji = "🍺",
            categoria = Categoria(id = CATEGORIA_CERVEJAS_ID, nome = "Cervejas")
        ),
        Produto(
            nome = "SKOL 600ml",
            preco = 9.5,
            emoji = "🍺",
            categoria = Categoria(id = CATEGORIA_CERVEJAS_ID, nome = "Cervejas")
        ),
        Produto(
            nome = "STELLA 350ml (LATA)",
            preco = 7.0,
            emoji = "🍺",
            categoria = Categoria(id = CATEGORIA_CERVEJAS_ID, nome = "Cervejas")
        ),
        Produto(
            nome = "STELLA 600ml",
            preco = 13.0,
            emoji = "🍺",
            categoria = Categoria(id = CATEGORIA_CERVEJAS_ID, nome = "Cervejas")
        ),
        Produto(
            nome = "CACHAÇA 51 (1/4)",
            preco = 15.0,
            emoji = "🥃",
            categoria = Categoria(id = CATEGORIA_DESTILADOS_ID, nome = "Destilados")
        ),
        Produto(
            nome = "CONHAQUE (1/4)",
            preco = 18.0,
            emoji = "🥃",
            categoria = Categoria(id = CATEGORIA_DESTILADOS_ID, nome = "Destilados")
        ),
        Produto(
            nome = "VODKA (1/4)",
            preco = 20.0,
            emoji = "🥃",
            categoria = Categoria(id = CATEGORIA_DESTILADOS_ID, nome = "Destilados")
        ),
        Produto(
            nome = "VINHO TINTO",
            preco = 22.0,
            emoji = "🍷",
            categoria = Categoria(id = CATEGORIA_VINHOS_ID, nome = "Vinhos")
        ),
        Produto(
            nome = "COCA-COLA (LATA)",
            preco = 5.0,
            emoji = "🥤",
            categoria = Categoria(id = CATEGORIA_REFRIGERANTES_ID, nome = "Refrigerantes")
        ),
        Produto(
            nome = "COCA-COLA 1L",
            preco = 7.5,
            emoji = "🥤",
            categoria = Categoria(id = CATEGORIA_REFRIGERANTES_ID, nome = "Refrigerantes")
        ),
        Produto(
            nome = "COCA-COLA 1,5L",
            preco = 9.0,
            emoji = "🥤",
            categoria = Categoria(id = CATEGORIA_REFRIGERANTES_ID, nome = "Refrigerantes")
        ),
        Produto(
            nome = "COCA-COLA 2L",
            preco = 10.5,
            emoji = "🥤",
            categoria = Categoria(id = CATEGORIA_REFRIGERANTES_ID, nome = "Refrigerantes")
        ),
        Produto(
            nome = "FANTA LARANJA",
            preco = 5.0,
            emoji = "🥤",
            categoria = Categoria(id = CATEGORIA_REFRIGERANTES_ID, nome = "Refrigerantes")
        ),
        Produto(
            nome = "GUARANÁ ANTARCTICA (LATA)",
            preco = 5.0,
            emoji = "🥤",
            categoria = Categoria(id = CATEGORIA_REFRIGERANTES_ID, nome = "Refrigerantes")
        ),
        Produto(
            nome = "SPRITE",
            preco = 5.0,
            emoji = "🥤",
            categoria = Categoria(id = CATEGORIA_REFRIGERANTES_ID, nome = "Refrigerantes")
        ),
        Produto(
            nome = "ÁGUA COM GÁS",
            preco = 3.5,
            emoji = "💧",
            categoria = Categoria(id = CATEGORIA_AGUA_ID, nome = "Água")
        ),
        Produto(
            nome = "ÁGUA SEM GÁS",
            preco = 3.0,
            emoji = "💧",
            categoria = Categoria(id = CATEGORIA_AGUA_ID, nome = "Água")
        ),
        Produto(
            nome = "AMENDOIM",
            preco = 6.0,
            emoji = "🍟",
            categoria = Categoria(id = CATEGORIA_PETISCOS_ID, nome = "Petiscos")
        ),
        Produto(
            nome = "CALABRESA",
            preco = 18.0,
            emoji = "🍟",
            categoria = Categoria(id = CATEGORIA_PETISCOS_ID, nome = "Petiscos")
        ),
        Produto(
            nome = "SALGADINHO",
            preco = 5.0,
            emoji = "🍟",
            categoria = Categoria(id = CATEGORIA_PETISCOS_ID, nome = "Petiscos")
        ),
        Produto(
            nome = "BOMBONS",
            preco = 4.0,
            emoji = "🍬",
            categoria = Categoria(id = CATEGORIA_DOCES_ID, nome = "Doces")
        ),
        Produto(
            nome = "CHICLETES",
            preco = 2.0,
            emoji = "🍬",
            categoria = Categoria(id = CATEGORIA_DOCES_ID, nome = "Doces")
        )
    )
}
