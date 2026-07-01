package com.time.applauncher.addict.core.designsystem.component

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer

/** Slow scale + alpha breathing used by the splash / focus dots. Animates below recomposition. */
fun Modifier.breathe(durationMillis: Int = 3400): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "breathe")
    val scale by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.14f,
        animationSpec = infiniteRepeatable(tween(durationMillis), RepeatMode.Reverse),
        label = "breatheScale"
    )
    val alpha by transition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(durationMillis), RepeatMode.Reverse),
        label = "breatheAlpha"
    )
    graphicsLayer {
        scaleX = scale
        scaleY = scale
        this.alpha = alpha
    }
}

/** Gentle opacity pulse for "tap to begin" / loading dots. */
fun Modifier.pulse(durationMillis: Int = 2400): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "pulse")
    val alpha by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(tween(durationMillis), RepeatMode.Reverse),
        label = "pulseAlpha"
    )
    graphicsLayer { this.alpha = alpha }
}
