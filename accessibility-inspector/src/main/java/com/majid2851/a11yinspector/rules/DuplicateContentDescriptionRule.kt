package com.majid2851.a11yinspector.rules

import com.majid2851.a11yinspector.model.A11yIssue
import com.majid2851.a11yinspector.model.IssueType
import com.majid2851.a11yinspector.model.Severity
import com.majid2851.a11yinspector.scanner.ScannedNode

/**
 * Flags interactive nodes that share an identical content description. Duplicate
 * labels make it impossible for screen-reader users to tell controls apart
 * (e.g. several buttons all announced as "More").
 */
class DuplicateContentDescriptionRule : A11yRule {

    override fun analyze(nodes: List<ScannedNode>, context: ScanContext): List<A11yIssue> {
        val interactive = nodes.filter { it.isInteractive && !it.isHidden && it.hasSize }
        val byDescription = interactive
            .mapNotNull { node -> node.contentDescription?.trim()?.lowercase()?.let { it to node } }
            .groupBy({ it.first }, { it.second })

        return byDescription
            .filterValues { it.size > 1 }
            .flatMap { (_, group) -> group.map { it.toIssue(group.size) } }
    }

    private fun ScannedNode.toIssue(occurrences: Int): A11yIssue = A11yIssue(
        type = IssueType.DuplicateContentDescription,
        severity = Severity.Info,
        title = "Duplicate content description",
        description = "The description \"${contentDescription}\" is used by $occurrences interactive elements, " +
            "so they are indistinguishable to screen readers.",
        suggestion = "Give each control a unique, specific description.",
        bounds = bounds,
        nodeLabel = label,
    )
}
