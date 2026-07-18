package com.majid2851.a11yinspector.model

/**
 * The category of accessibility problem detected by a rule.
 *
 * Each type maps to a single rule but is kept separate from the rule classes so
 * that reports, filtering, and analytics can reference a stable identifier.
 */
enum class IssueType(val displayName: String) {
    MissingContentDescription("Missing content description"),
    SmallTouchTarget("Touch target too small"),
    LowContrast("Low color contrast"),
    DuplicateContentDescription("Duplicate content description"),
    RedundantDescription("Redundant description wording"),
}
