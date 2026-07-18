package com.majid2851.a11yinspector.model

import androidx.compose.ui.graphics.Color

/**
 * Severity of an accessibility [A11yIssue], ordered from most to least critical.
 */
enum class Severity(val label: String, val color: Color) {
    /** Blocks or seriously degrades usage with assistive technologies. */
    Error(label = "Error", color = Color(0xFFE53935)),

    /** Likely to cause friction for some users; should be fixed. */
    Warning(label = "Warning", color = Color(0xFFFB8C00)),

    /** Best-practice hint that improves the experience. */
    Info(label = "Info", color = Color(0xFF1E88E5)),
}
