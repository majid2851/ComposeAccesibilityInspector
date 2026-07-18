package com.majid2851.a11yinspector.rules

/**
 * The default rule set used by the inspector when no custom rules are supplied.
 *
 * Callers can pass their own list to `AccessibilityInspector` to add, remove, or
 * reorder checks.
 */
fun defaultRules(): List<A11yRule> = listOf(
    MissingContentDescriptionRule(),
    TouchTargetSizeRule(),
    DuplicateContentDescriptionRule(),
    RedundantDescriptionRule(),
    ContrastRule(),
)
