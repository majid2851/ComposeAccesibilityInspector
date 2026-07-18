package com.majid2851.a11yinspector

/**
 * Tuning options for the accessibility inspector.
 *
 * All values have sensible defaults aligned with the Android accessibility and
 * WCAG 2.1 AA guidelines, so most callers can use `InspectorConfig()` as-is.
 *
 * @property minTouchTargetDp minimum width/height for interactive targets. The
 *   Material guidance is 48dp.
 * @property minContrastRatio minimum contrast ratio between text and its
 *   background. WCAG AA requires 4.5:1 for normal text.
 * @property checkContrast whether to run the (approximate, pixel-sampling)
 *   contrast rule. Disable it if you only want the fast semantic checks.
 * @property showOverlayOnStart whether highlights are visible when the inspector
 *   first appears.
 * @property autoRescanMillis if greater than zero, the inspector re-scans on this
 *   interval (useful for animated screens). Zero means scan-on-demand only.
 */
data class InspectorConfig(
    val minTouchTargetDp: Int = 48,
    val minContrastRatio: Float = 4.5f,
    val checkContrast: Boolean = true,
    val showOverlayOnStart: Boolean = true,
    val autoRescanMillis: Long = 0L,
)
