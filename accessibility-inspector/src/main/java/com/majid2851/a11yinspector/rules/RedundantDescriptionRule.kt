package com.majid2851.a11yinspector.rules

import com.majid2851.a11yinspector.model.A11yIssue
import com.majid2851.a11yinspector.model.IssueType
import com.majid2851.a11yinspector.model.Severity
import com.majid2851.a11yinspector.scanner.ScannedNode

/**
 * Flags content descriptions that restate the element's role (e.g. "Play
 * button", "Profile image"). Screen readers already announce the role, so these
 * words are read twice ("Play button, button").
 */
class RedundantDescriptionRule : A11yRule {

    override fun analyze(nodes: List<ScannedNode>, context: ScanContext): List<A11yIssue> =
        nodes.mapNotNull { node ->
            if (node.isHidden) return@mapNotNull null
            val description = node.contentDescription ?: return@mapNotNull null
            val offending = REDUNDANT_WORDS.firstOrNull { word ->
                description.split(NON_WORD).any { it.equals(word, ignoreCase = true) }
            } ?: return@mapNotNull null

            A11yIssue(
                type = IssueType.RedundantDescription,
                severity = Severity.Info,
                title = "Redundant description wording",
                description = "The description \"$description\" contains \"$offending\", which screen readers " +
                    "already announce from the element's role.",
                suggestion = "Remove role words like \"$offending\" from the content description.",
                bounds = node.bounds,
                nodeLabel = node.label,
            )
        }

    private companion object {
        val REDUNDANT_WORDS = listOf("button", "image", "icon", "graphic", "picture")
        val NON_WORD = Regex("[^\\p{L}]+")
    }
}
