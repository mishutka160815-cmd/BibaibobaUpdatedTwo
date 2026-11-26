package com.bibaiboba.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.bibaiboba.app.ui.theme.BgDeep
import com.bibaiboba.app.ui.theme.BgMid
import com.bibaiboba.app.ui.theme.NeonCyan
import com.bibaiboba.app.ui.theme.NeonPink
import com.bibaiboba.app.ui.theme.NeonPurple

@Composable
fun AnimatedBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val transition = rememberInfiniteTransition(label = "bg")
    val shift1 by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(12000, easing = LinearEasing), RepeatMode.Reverse),
        label = "s1"
    )
    val shift2 by transition.animateFloat(
        initialValue = 1f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(15000, easing = LinearEasing), RepeatMode.Reverse),
        label = "s2"
    )
    val shift3 by transition.animateFloat(
        initialValue = 0.2f, targetValue = 0.8f,
        animationSpec = infiniteRepeatable(tween(10000, easing = LinearEasing), RepeatMode.Reverse),
        label = "s3"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BgDeep, BgMid, Color(0xFF0A0C20))))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(NeonPurple.copy(alpha = 0.28f), Color.Transparent),
                    center = Offset(w * (0.15f + 0.2f * shift1), h * (0.15f + 0.1f * shift2)),
                    radius = w * 0.65f
                ),
                radius = w * 0.65f,
                center = Offset(w * (0.15f + 0.2f * shift1), h * (0.15f + 0.1f * shift2))
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(NeonCyan.copy(alpha = 0.18f), Color.Transparent),
                    center = Offset(w * (0.85f - 0.15f * shift2), h * (0.35f + 0.15f * shift1)),
                    radius = w * 0.55f
                ),
                radius = w * 0.55f,
                center = Offset(w * (0.85f - 0.15f * shift2), h * (0.35f + 0.15f * shift1))
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(NeonPink.copy(alpha = 0.16f), Color.Transparent),
                    center = Offset(w * (0.5f + 0.1f * shift3), h * (0.85f - 0.1f * shift1)),
                    radius = w * 0.7f
                ),
                radius = w * 0.7f,
                center = Offset(w * (0.5f + 0.1f * shift3), h * (0.85f - 0.1f * shift1))
            )
        }
        content()
    }
}
