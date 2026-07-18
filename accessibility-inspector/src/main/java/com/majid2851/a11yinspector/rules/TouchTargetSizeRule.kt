package com.majid2851.a11yinspector.rules

import com.majid2851.a11yinspector.model.A11yIssue
import com.majid2851.a11yinspector.model.IssueType
import com.majid2851.a11yinspector.model.Severity
import com.majid2851.a11yinspector.scanner.ScannedNode
import kotlin.math.roundToInt

/**
 * Flags interactive nodes whose measured width or height is smaller than the
 * configured minimum touch target (48dp by default).
 */
class TouchTargetSizeRule : A11yRule {

    override fun analyze(nodes: List<ScannedNode>, context: ScanContext): List<A11yIssue> {
        val minDp = context.config.minTouchTargetDp
        val minPx = minDp * context.density
        return nodes.mapNotNull { node ->
            if (node.isHidden || !node.hasSize || !node.isInteractive) return@mapNotNull null
            if (node.widthPx >= minPx && node.heightPx >= minPx) return@mapNotNull null

            val wDp = (node.widthPx / context.density).roundToInt()
            val hDp = (node.heightPx / context.density).roundToInt()
            A11yIssue(
                type = IssueType.SmallTouchTarget,
                severity = Severity.Warning,
                title = "Touch target too small",
                description = "This target is ${wDp}dp × ${hDp}dp, below the recommended ${minDp}dp minimum.",
                suggestion = "Increase the size, add padding, or use Modifier.minimumInteractiveComponentSize().",
                bounds = node.bounds,
                nodeLabel = node.label,
            )
        }
    }
}
