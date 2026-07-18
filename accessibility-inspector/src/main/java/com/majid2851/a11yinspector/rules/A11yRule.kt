package com.majid2851.a11yinspector.rules

import com.majid2851.a11yinspector.InspectorConfig
import com.majid2851.a11yinspector.model.A11yIssue
import com.majid2851.a11yinspector.scanner.LuminanceSampler
import com.majid2851.a11yinspector.scanner.ScannedNode

/**
 * Context passed to every rule during a scan.
 *
 * @property config active inspector configuration and thresholds.
 * @property density screen density (px per dp) for px<->dp conversions.
 * @property luminanceSampler pixel sampler used by the contrast rule, or null
 *   when contrast checking is disabled or capture failed.
 */
class ScanContext(
    val config: InspectorConfig,
    val density: Float,
    val luminanceSampler: LuminanceSampler?,
)

/**
 * A single accessibility check.
 *
 * Rules receive the full list of scanned nodes so that both per-node checks and
 * cross-node checks (such as duplicate descriptions) can be expressed. Rules must
 * be pure: given the same input they must return the same issues.
 */
fun interface A11yRule {
    fun analyze(nodes: List<ScannedNode>, context: ScanContext): List<A11yIssue>
}
