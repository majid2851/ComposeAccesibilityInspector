package com.majid2851.a11yinspector.model

import androidx.compose.ui.geometry.Rect

/**
 * A single accessibility problem found on screen.
 *
 * @property type stable category of the problem.
 * @property severity how serious the problem is.
 * @property title short, human-readable headline shown in the panel.
 * @property description longer explanation of what is wrong.
 * @property suggestion concrete guidance on how to fix it.
 * @property bounds position of the offending node in root (pixel) coordinates,
 *   used to draw the highlight overlay.
 * @property nodeLabel best-effort label of the node (text, description, role or
 *   test tag) to help the developer locate it.
 */
data class A11yIssue(
    val type: IssueType,
    val severity: Severity,
    val title: String,
    val description: String,
    val suggestion: String,
    val bounds: Rect,
    val nodeLabel: String?,
)
