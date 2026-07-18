package com.majid2851.a11yinspector.rules

import com.majid2851.a11yinspector.model.A11yIssue
import com.majid2851.a11yinspector.model.IssueType
import com.majid2851.a11yinspector.model.Severity
import com.majid2851.a11yinspector.scanner.Contrast
import com.majid2851.a11yinspector.scanner.ScannedNode
import kotlin.math.roundToInt

/**
 * Approximate color-contrast check.
 *
 * For each text node it samples the rendered pixels within the node bounds and
 * estimates the contrast ratio between the darkest and lightest clusters. This
 * is a heuristic: it assumes the text and its background dominate the region and
 * is only used for guidance, not certification.
 *
 * Requires a [ScanContext.luminanceSampler]; otherwise it returns nothing.
 */
class ContrastRule : A11yRule {

    override fun analyze(nodes: List<ScannedNode>, context: ScanContext): List<A11yIssue> {
        val sampler = context.luminanceSampler ?: return emptyList()
        val minRatio = context.config.minContrastRatio

        return nodes.mapNotNull { node ->
            if (node.isHidden || !node.hasSize) return@mapNotNull null
            if (node.text.isNullOrBlank()) return@mapNotNull null

            val samples = sampler.sample(
                left = node.bounds.left.roundToInt(),
                top = node.bounds.top.roundToInt(),
                right = node.bounds.right.roundToInt(),
                bottom = node.bounds.bottom.roundToInt(),
            )
            val ratio = Contrast.estimateRatioFromSamples(samples) ?: return@mapNotNull null
            if (ratio >= minRatio) return@mapNotNull null

            A11yIssue(
                type = IssueType.LowContrast,
                severity = Severity.Warning,
                title = "Low color contrast",
                description = "Estimated contrast ratio is ${format(ratio)}:1, below the ${format(minRatio.toDouble())}:1 " +
                    "minimum for readable text.",
                suggestion = "Increase the contrast between the text color and its background.",
                bounds = node.bounds,
                nodeLabel = node.label,
            )
        }
    }

    private fun format(value: Double): String = ((value * 10).roundToInt() / 10.0).toString()
}
