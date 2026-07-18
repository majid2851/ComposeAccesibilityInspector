package com.majid2851.a11yinspector.report

import com.majid2851.a11yinspector.model.A11yIssue
import com.majid2851.a11yinspector.model.Severity

/**
 * A summary of the issues found in a single scan, with helpers to export the
 * results (for example to a PR comment or CI log).
 */
class A11yReport(val issues: List<A11yIssue>) {

    val errorCount: Int get() = issues.count { it.severity == Severity.Error }
    val warningCount: Int get() = issues.count { it.severity == Severity.Warning }
    val infoCount: Int get() = issues.count { it.severity == Severity.Info }
    val isClean: Boolean get() = issues.isEmpty()

    /** One-line human summary such as "2 errors, 1 warning, 0 info". */
    fun summary(): String =
        "$errorCount error${plural(errorCount)}, " +
            "$warningCount warning${plural(warningCount)}, " +
            "$infoCount info"

    /** Renders the report as Markdown, suitable for logs or PR comments. */
    fun toMarkdown(): String = buildString {
        appendLine("## Accessibility report")
        appendLine()
        appendLine(summary())
        if (issues.isEmpty()) {
            appendLine()
            appendLine("No accessibility issues found.")
            return@buildString
        }
        appendLine()
        appendLine("| Severity | Type | Element | Detail |")
        appendLine("| --- | --- | --- | --- |")
        issues.forEach { issue ->
            appendLine(
                "| ${issue.severity.label} | ${issue.type.displayName} | " +
                    "${issue.nodeLabel ?: "-"} | ${issue.description} |",
            )
        }
    }

    private fun plural(count: Int) = if (count == 1) "" else "s"
}
