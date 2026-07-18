package com.majid2851.a11yinspector

import androidx.compose.ui.geometry.Rect
import com.majid2851.a11yinspector.rules.ScanContext
import com.majid2851.a11yinspector.scanner.LuminanceSampler
import com.majid2851.a11yinspector.scanner.ScannedNode

/** Test helpers for building [ScannedNode]s and a [ScanContext] concisely. */
internal fun scannedNode(
    id: Int = 0,
    bounds: Rect = Rect(0f, 0f, 100f, 100f),
    widthPx: Int = 100,
    heightPx: Int = 100,
    isClickable: Boolean = false,
    isToggleable: Boolean = false,
    isSelectable: Boolean = false,
    isEnabled: Boolean = true,
    isHidden: Boolean = false,
    role: String? = null,
    contentDescriptions: List<String> = emptyList(),
    texts: List<String> = emptyList(),
    testTag: String? = null,
): ScannedNode = ScannedNode(
    id = id,
    bounds = bounds,
    widthPx = widthPx,
    heightPx = heightPx,
    isClickable = isClickable,
    isToggleable = isToggleable,
    isSelectable = isSelectable,
    isEnabled = isEnabled,
    isHidden = isHidden,
    role = role,
    contentDescriptions = contentDescriptions,
    texts = texts,
    testTag = testTag,
)

internal fun scanContext(
    density: Float = 1f,
    config: InspectorConfig = InspectorConfig(),
    luminanceSampler: LuminanceSampler? = null,
): ScanContext = ScanContext(config = config, density = density, luminanceSampler = luminanceSampler)
