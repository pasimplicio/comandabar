package com.example.comandabar.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Cores das categorias
val CorCerveja = Color(0xFFF57C00)    // Laranja
val CorWhisky = Color(0xFF795548)     // Marrom
val CorRefrigerante = Color(0xFF2196F3) // Azul
val CorSuco = Color(0xFF4CAF50)       // Verde
val CorEnergetico = Color(0xFF9C27B0) // Roxo

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBrown,
    secondary = AccentBrown,
    tertiary = AccentBrownLight,
    background = Color(0xFF1B1413),
    surface = Color(0xFF261D1C),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color(0xFF201514),
    onBackground = Color(0xFFEFE3E1),
    onSurface = Color(0xFFEFE3E1),
    primaryContainer = PrimaryBrownDark,
    secondaryContainer = Color(0xFF3A2A28)
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBrown,
    secondary = AccentBrown,
    tertiary = AccentBrownLight,

    background = BackgroundCream,
    surface = SurfacePink,

    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = PrimaryBrownDark,

    onBackground = Color(0xFF2A1F1E),
    onSurface = Color(0xFF2A1F1E),

    // Usado em "caixinhas" de ícone e alguns cards de resumo
    primaryContainer = SurfacePinkAlt,
    secondaryContainer = SurfacePinkAlt
)

@Composable
fun ComandaBarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Importante: o layout do app depende da paleta fixa. Dynamic color quebra a identidade visual.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()

            // A barra é escura no layout, então os ícones devem ser claros.
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
