package com.majid2851.a11yinspector.scanner

import android.view.View
import com.majid2851.a11yinspector.InspectorConfig
import com.majid2851.a11yinspector.model.A11yIssue
import com.majid2851.a11yinspector.rules.A11yRule
import com.majid2851.a11yinspector.rules.ScanContext

/**
 * Orchestrates a single accessibility scan: read the semantics tree, optionally
 * capture a bitmap for contrast sampling, then run every rule.
 *
 * Must be called on the main thread because it touches the view hierarchy.
 */
object A11yScanEngine {

    fun scan(
        view: View,
        density: Float,
        config: InspectorConfig,
        rules: List<A11yRule>,
    ): List<A11yIssue> {
        val nodes = SemanticsScanner.scan(view)
        if (nodes.isEmpty()) return emptyList()

        val sampler = if (config.checkContrast) BitmapLuminanceSampler.capture(view) else null
        val context = ScanContext(config = config, density = density, luminanceSampler = sampler)

        return rules
            .flatMap { rule -> runCatching { rule.analyze(nodes, context) }.getOrDefault(emptyList()) }
            .sortedBy { it.severity.ordinal }
    }
}
