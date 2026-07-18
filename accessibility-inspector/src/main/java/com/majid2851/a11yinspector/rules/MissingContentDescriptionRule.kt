package com.majid2851.a11yinspector.rules

import com.majid2851.a11yinspector.model.A11yIssue
import com.majid2851.a11yinspector.model.IssueType
import com.majid2851.a11yinspector.model.Severity
import com.majid2851.a11yinspector.scanner.ScannedNode

/**
 * Flags interactive nodes and images that expose no textual label, meaning
 * screen readers announce nothing meaningful for them.
 */
class MissingContentDescriptionRule : A11yRule {

    override fun analyze(nodes: List<ScannedNode>, context: ScanContext): List<A11yIssue> =
        nodes.filter(::isViolation).map { node ->
            A11yIssue(
                type = IssueType.MissingContentDescription,
                severity = Severity.Error,
                title = "Missing content description",
                description = buildString {
                    append(if (node.role == ROLE_IMAGE) "This image" else "This interactive element")
                    append(" has no text or content description, so assistive technologies cannot describe it.")
                },
                suggestion = "Add a contentDescription (or visible text). Use null only for purely decorative elements.",
                bounds = node.bounds,
                nodeLabel = node.label,
            )
        }

    private fun isViolation(node: ScannedNode): Boolean {
        if (node.isHidden || !node.hasSize) return false
        val relevant = node.isInteractive || node.role == ROLE_IMAGE
        return relevant && !node.hasLabel
    }

    private companion object {
        const val ROLE_IMAGE = "Image"
    }
}
