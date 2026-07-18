package com.majid2851.a11yinspector

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import com.majid2851.a11yinspector.model.A11yIssue
import com.majid2851.a11yinspector.overlay.HighlightOverlay
import com.majid2851.a11yinspector.overlay.InspectorPanel
import com.majid2851.a11yinspector.overlay.IssueFilter
import com.majid2851.a11yinspector.report.A11yReport
import com.majid2851.a11yinspector.rules.A11yRule
import com.majid2851.a11yinspector.rules.defaultRules
import com.majid2851.a11yinspector.scanner.A11yScanEngine
import kotlinx.coroutines.delay

/**
 * Wraps your UI with a live accessibility inspector — like Lighthouse, but for
 * Jetpack Compose.
 *
 * It scans the Compose semantics tree, highlights accessibility problems
 * directly on screen, and shows a filterable panel of issues. It is intended for
 * debug builds; gate it behind `BuildConfig.DEBUG` and prefer wiring the
 * dependency in via `debugImplementation` so it never ships to production.
 *
 * ```
 * AccessibilityInspector(enabled = BuildConfig.DEBUG) {
 *     MyApp()
 * }
 * ```
 *
 * @param enabled when false the [content] is rendered untouched with zero cost.
 * @param config thresholds and behavior (touch target size, contrast, etc.).
 * @param rules the checks to run; defaults to [defaultRules].
 * @param onReport invoked after every scan with the latest [A11yReport], useful
 *   for logging or assertions.
 * @param content your app UI.
 */
@Composable
fun AccessibilityInspector(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    config: InspectorConfig = InspectorConfig(),
    rules: List<A11yRule> = defaultRules(),
    onReport: (A11yReport) -> Unit = {},
    content: @Composable () -> Unit,
) {
    if (!enabled) {
        content()
        return
    }

    val view = LocalView.current
    val density = LocalDensity.current.density
    val currentOnReport by rememberUpdatedState(onReport)

    var issues by remember { mutableStateOf<List<A11yIssue>>(emptyList()) }
    var scanTrigger by remember { mutableIntStateOf(0) }
    var expanded by remember { mutableStateOf(false) }
    var overlayVisible by remember { mutableStateOf(config.showOverlayOnStart) }
    var filter by remember { mutableStateOf(IssueFilter.All) }

    LaunchedEffect(scanTrigger, config, rules) {
        // Let the content lay out and settle before reading the semantics tree.
        withFrameNanos { }
        delay(SETTLE_DELAY_MS)
        val result = A11yScanEngine.scan(view, density, config, rules)
        issues = result
        currentOnReport(A11yReport(result))

        if (config.autoRescanMillis > 0L) {
            while (true) {
                delay(config.autoRescanMillis)
                val periodic = A11yScanEngine.scan(view, density, config, rules)
                issues = periodic
                currentOnReport(A11yReport(periodic))
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        content()

        if (overlayVisible) {
            HighlightOverlay(issues = issues)
        }

        InspectorPanel(
            report = A11yReport(issues),
            expanded = expanded,
            filter = filter,
            overlayVisible = overlayVisible,
            onToggleExpanded = { expanded = !expanded },
            onFilterChange = { filter = it },
            onToggleOverlay = { overlayVisible = !overlayVisible },
            onRescan = { scanTrigger++ },
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

private const val SETTLE_DELAY_MS = 200L
