package com.majid2851.a11yinspector

import androidx.compose.ui.geometry.Rect
import com.majid2851.a11yinspector.model.IssueType
import com.majid2851.a11yinspector.rules.DuplicateContentDescriptionRule
import com.majid2851.a11yinspector.rules.MissingContentDescriptionRule
import com.majid2851.a11yinspector.rules.RedundantDescriptionRule
import com.majid2851.a11yinspector.rules.TouchTargetSizeRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RulesTest {

    private val context = scanContext()

    @Test
    fun `missing description flags unlabeled clickable`() {
        val nodes = listOf(scannedNode(isClickable = true))
        val issues = MissingContentDescriptionRule().analyze(nodes, context)
        assertEquals(1, issues.size)
        assertEquals(IssueType.MissingContentDescription, issues.first().type)
    }

    @Test
    fun `missing description ignores labeled clickable`() {
        val nodes = listOf(scannedNode(isClickable = true, contentDescriptions = listOf("Save")))
        assertTrue(MissingContentDescriptionRule().analyze(nodes, context).isEmpty())
    }

    @Test
    fun `missing description flags unlabeled image`() {
        val nodes = listOf(scannedNode(role = "Image"))
        assertEquals(1, MissingContentDescriptionRule().analyze(nodes, context).size)
    }

    @Test
    fun `missing description ignores hidden node`() {
        val nodes = listOf(scannedNode(isClickable = true, isHidden = true))
        assertTrue(MissingContentDescriptionRule().analyze(nodes, context).isEmpty())
    }

    @Test
    fun `small touch target flagged below 48dp`() {
        val nodes = listOf(
            scannedNode(
                isClickable = true,
                widthPx = 40,
                heightPx = 40,
                bounds = Rect(0f, 0f, 40f, 40f),
            ),
        )
        val issues = TouchTargetSizeRule().analyze(nodes, context)
        assertEquals(1, issues.size)
        assertEquals(IssueType.SmallTouchTarget, issues.first().type)
    }

    @Test
    fun `touch target at 48dp passes`() {
        val nodes = listOf(scannedNode(isClickable = true, widthPx = 48, heightPx = 48))
        assertTrue(TouchTargetSizeRule().analyze(nodes, context).isEmpty())
    }

    @Test
    fun `duplicate descriptions flag every offender`() {
        val nodes = listOf(
            scannedNode(id = 1, isClickable = true, contentDescriptions = listOf("More")),
            scannedNode(id = 2, isClickable = true, contentDescriptions = listOf("more")),
            scannedNode(id = 3, isClickable = true, contentDescriptions = listOf("Unique")),
        )
        val issues = DuplicateContentDescriptionRule().analyze(nodes, context)
        assertEquals(2, issues.size)
        assertTrue(issues.all { it.type == IssueType.DuplicateContentDescription })
    }

    @Test
    fun `redundant wording flagged`() {
        val nodes = listOf(scannedNode(isClickable = true, contentDescriptions = listOf("Play button")))
        val issues = RedundantDescriptionRule().analyze(nodes, context)
        assertEquals(1, issues.size)
        assertEquals(IssueType.RedundantDescription, issues.first().type)
    }

    @Test
    fun `non-redundant wording passes`() {
        val nodes = listOf(scannedNode(isClickable = true, contentDescriptions = listOf("Play")))
        assertTrue(RedundantDescriptionRule().analyze(nodes, context).isEmpty())
    }
}
