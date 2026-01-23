package com.example.comandabar.ui.animations

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.shape.RoundedCornerShape

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ScaleInTransition(
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = true,
        transitionSpec = {
            scaleIn(animationSpec = tween(300)) togetherWith
                    scaleOut(animationSpec = tween(300))
        },
        modifier = modifier
    ) {
        content()
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FadeInTransition(
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = true,
        transitionSpec = {
            fadeIn(animationSpec = tween(300)) togetherWith
                    fadeOut(animationSpec = tween(300))
        },
        modifier = modifier
    ) {
        content()
    }
}

// Transição combinada (scale + fade)
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ElegantTransition(
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = true,
        transitionSpec = {
            (scaleIn(animationSpec = tween(350)) +
                    fadeIn(animationSpec = tween(350))) togetherWith
                    (scaleOut(animationSpec = tween(350)) +
                            fadeOut(animationSpec = tween(350)))
        },
        modifier = modifier
    ) {
        content()
    }
}

// Transição de shimmer (loading)
@Composable
fun ShimmerTransition(
    isLoading: Boolean,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    )

    val transition = rememberInfiniteTransition()
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = LinearEasing
            )
        )
    )

    Box(modifier = modifier) {
        content()

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = shimmerColors,
                            start = androidx.compose.ui.geometry.Offset(
                                x = translateAnim.value - 500,
                                y = 0f
                            ),
                            end = androidx.compose.ui.geometry.Offset(
                                x = translateAnim.value,
                                y = 100f
                            )
                        )
                    )
            )
        }
    }
}

// Efeito de pulso para elementos importantes
@Composable
fun PulseEffect(
    content: @Composable () -> Unit,
    isActive: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition()
    val pulseValue by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier.scale(pulseValue)
    ) {
        content()
    }
}

// Transição de página com fundo gradient
@Composable
fun GradientPageTransition(
    content: @Composable () -> Unit
) {
    val gradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
            Color.Transparent,
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        content()
    }
}