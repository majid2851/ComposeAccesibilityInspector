package com.majid2851.a11yinspector.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.majid2851.a11yinspector.model.A11yIssue
import com.majid2851.a11yinspector.model.Severity
import com.majid2851.a11yinspector.report.A11yReport

/** The severities a user can filter the issue list by. */
internal enum class IssueFilter(val label: String, val severity: Severity?) {
    All("All", null),
    Errors("Errors", Severity.Error),
    Warnings("Warnings", Severity.Warning),
    Info("Info", Severity.Info),
}

/**
 * The floating inspector panel: a summary bar that expands into a filterable
 * list of issues, plus rescan and overlay-toggle controls.
 */
@Composable
internal fun InspectorPanel(
    report: A11yReport,
    expanded: Boolean,
    filter: IssueFilter,
    overlayVisible: Boolean,
    onToggleExpanded: () -> Unit,
    onFilterChange: (IssueFilter) -> Unit,
    onToggleOverlay: () -> Unit,
    onRescan: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        shadowElevation = 12.dp,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            SummaryBar(
                report = report,
                expanded = expanded,
                onToggleExpanded = onToggleExpanded,
            )

            if (expanded) {
                Spacer(Modifier.size(8.dp))
                ControlRow(
                    overlayVisible = overlayVisible,
                    onToggleOverlay = onToggleOverlay,
                    onRescan = onRescan,
                )
                Spacer(Modifier.size(8.dp))
                FilterRow(filter = filter, report = report, onFilterChange = onFilterChange)
                Spacer(Modifier.size(8.dp))
                HorizontalDivider()
                IssueList(report = report, filter = filter)
            }
        }
    }
}

@Composable
private fun SummaryBar(
    report: A11yReport,
    expanded: Boolean,
    onToggleExpanded: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Accessibility",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.size(12.dp))
        SeverityCount(Severity.Error, report.errorCount)
        Spacer(Modifier.size(8.dp))
        SeverityCount(Severity.Warning, report.warningCount)
        Spacer(Modifier.size(8.dp))
        SeverityCount(Severity.Info, report.infoCount)
        Spacer(Modifier.weight(1f))
        TextButton(onClick = onToggleExpanded) {
            Text(if (expanded) "Hide" else "Details")
        }
    }
}

@Composable
private fun SeverityCount(severity: Severity, count: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(severity.color),
        )
        Spacer(Modifier.size(4.dp))
        Text(text = count.toString(), style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun ControlRow(
    overlayVisible: Boolean,
    onToggleOverlay: () -> Unit,
    onRescan: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TextButton(onClick = onRescan) { Text("Rescan") }
        TextButton(onClick = onToggleOverlay) {
            Text(if (overlayVisible) "Hide highlights" else "Show highlights")
        }
    }
}

@Composable
private fun FilterRow(
    filter: IssueFilter,
    report: A11yReport,
    onFilterChange: (IssueFilter) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        IssueFilter.entries.forEach { entry ->
            val count = when (entry) {
                IssueFilter.All -> report.issues.size
                IssueFilter.Errors -> report.errorCount
                IssueFilter.Warnings -> report.warningCount
                IssueFilter.Info -> report.infoCount
            }
            FilterChip(
                selected = filter == entry,
                onClick = { onFilterChange(entry) },
                label = { Text("${entry.label} ($count)") },
            )
        }
    }
}

@Composable
private fun IssueList(report: A11yReport, filter: IssueFilter) {
    val visible = report.issues.filter { filter.severity == null || it.severity == filter.severity }
    if (visible.isEmpty()) {
        Text(
            text = "No issues for this filter.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(vertical = 16.dp),
        )
        return
    }
    LazyColumn(
        modifier = Modifier.heightIn(max = 280.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 8.dp),
    ) {
        items(visible) { issue -> IssueCard(issue) }
    }
}

@Composable
private fun IssueCard(issue: A11yIssue) {
    Surface(
        color = issue.severity.color.copy(alpha = 0.08f),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(modifier = Modifier.padding(10.dp)) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(issue.severity.color),
            )
            Spacer(Modifier.size(10.dp))
            Column {
                Text(
                    text = issue.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                issue.nodeLabel?.let {
                    Text(
                        text = "on \"$it\"",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    )
                }
                Spacer(Modifier.size(2.dp))
                Text(text = issue.description, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.size(2.dp))
                Text(
                    text = "Fix: ${issue.suggestion}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF2E7D32),
                )
            }
        }
    }
}
