package com.majid2851.a11yinspector.overlay

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import com.majid2851.a11yinspector.model.A11yIssue

/**
 * Draws a colored rectangle around every issue's bounds. The overlay does not
 * intercept touches, so the underlying UI stays fully interactive.
 */
@Composable
internal fun HighlightOverlay(
    issues: List<A11yIssue>,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        issues.forEach { issue ->
            val bounds = issue.bounds
            if (bounds.width <= 0f || bounds.height <= 0f) return@forEach
            val color = issue.severity.color
            drawRect(
                color = color.copy(alpha = 0.12f),
                topLeft = Offset(bounds.left, bounds.top),
                size = Size(bounds.width, bounds.height),
            )
            drawRect(
                color = color,
                topLeft = Offset(bounds.left, bounds.top),
                size = Size(bounds.width, bounds.height),
                style = Stroke(width = STROKE_WIDTH_PX),
            )
        }
    }
}

private const val STROKE_WIDTH_PX = 4f
