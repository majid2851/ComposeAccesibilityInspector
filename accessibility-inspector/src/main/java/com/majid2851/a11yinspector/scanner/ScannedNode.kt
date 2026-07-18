package com.majid2851.a11yinspector.scanner

import androidx.compose.ui.geometry.Rect

/**
 * A framework-independent snapshot of a single semantics node.
 *
 * Rules operate on [ScannedNode] rather than directly on Compose's `SemanticsNode`
 * so they stay pure and unit-testable without an Android/Compose runtime.
 *
 * @property id stable-per-scan identifier used to de-duplicate issues.
 * @property bounds node bounds in root (pixel) coordinates.
 * @property widthPx measured width in pixels.
 * @property heightPx measured height in pixels.
 * @property isClickable node exposes an on-click action.
 * @property isToggleable node exposes a toggleable state (switch/checkbox).
 * @property isSelectable node exposes a selected state (tab/chip).
 * @property isEnabled node is not marked disabled.
 * @property isHidden node is intentionally hidden from accessibility.
 * @property role semantic role name (e.g. "Button", "Image"), or null.
 * @property contentDescriptions merged content descriptions.
 * @property texts merged visible text.
 * @property testTag developer-assigned test tag, or null.
 */
data class ScannedNode(
    val id: Int,
    val bounds: Rect,
    val widthPx: Int,
    val heightPx: Int,
    val isClickable: Boolean,
    val isToggleable: Boolean,
    val isSelectable: Boolean,
    val isEnabled: Boolean,
    val isHidden: Boolean,
    val role: String?,
    val contentDescriptions: List<String>,
    val texts: List<String>,
    val testTag: String?,
) {
    /** True when the node participates in user interaction. */
    val isInteractive: Boolean
        get() = isClickable || isToggleable || isSelectable

    /** True when the node has a non-zero, laid-out size. */
    val hasSize: Boolean
        get() = widthPx > 0 && heightPx > 0

    /** The first non-blank content description, if any. */
    val contentDescription: String?
        get() = contentDescriptions.firstOrNull { it.isNotBlank() }

    /** The first non-blank text, if any. */
    val text: String?
        get() = texts.firstOrNull { it.isNotBlank() }

    /** True when the node exposes any non-blank label (text or description). */
    val hasLabel: Boolean
        get() = contentDescription != null || text != null

    /** Best-effort human label used to locate the node in the UI. */
    val label: String?
        get() = contentDescription ?: text ?: testTag ?: role
}
