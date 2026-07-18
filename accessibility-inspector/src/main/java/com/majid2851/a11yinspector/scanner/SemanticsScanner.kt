@file:OptIn(InternalComposeUiApi::class, ExperimentalComposeUiApi::class)

package com.majid2851.a11yinspector.scanner

import android.view.View
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.node.RootForTest
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsOwner
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull

/**
 * Walks the Compose semantics tree of a [View] and produces a flat list of
 * [ScannedNode]s that the rule engine can analyze.
 *
 * The Compose root view (`AndroidComposeView`) implements [RootForTest], which
 * exposes the [SemanticsOwner]. We traverse the merged tree because that mirrors
 * what assistive technologies actually observe.
 */
object SemanticsScanner {

    /**
     * @return the flattened merged semantics nodes, or an empty list if the view
     *   is not a Compose root or the tree is not yet available.
     */
    fun scan(view: View): List<ScannedNode> {
        val owner = view.semanticsOwnerOrNull() ?: return emptyList()
        return runCatching { collect(owner) }.getOrDefault(emptyList())
    }

    private fun View.semanticsOwnerOrNull(): SemanticsOwner? =
        (this as? RootForTest)?.semanticsOwner

    private fun collect(owner: SemanticsOwner): List<ScannedNode> {
        val result = ArrayList<ScannedNode>()
        var nextId = 0
        val stack = ArrayDeque<SemanticsNode>()
        stack.addLast(owner.rootSemanticsNode)
        while (stack.isNotEmpty()) {
            val node = stack.removeLast()
            result += node.toScannedNode(nextId++)
            // children are added in reverse so traversal stays top-to-bottom.
            for (index in node.children.indices.reversed()) {
                stack.addLast(node.children[index])
            }
        }
        return result
    }

    private fun SemanticsNode.toScannedNode(id: Int): ScannedNode {
        val descriptions = config.getOrNull(SemanticsProperties.ContentDescription)
            ?.filterNotNull()
            .orEmpty()
        val texts = config.getOrNull(SemanticsProperties.Text)
            ?.map { it.text }
            .orEmpty()
        return ScannedNode(
            id = id,
            bounds = boundsInRoot,
            widthPx = size.width,
            heightPx = size.height,
            isClickable = config.contains(SemanticsActions.OnClick),
            isToggleable = config.contains(SemanticsProperties.ToggleableState),
            isSelectable = config.contains(SemanticsProperties.Selected),
            isEnabled = !config.contains(SemanticsProperties.Disabled),
            isHidden = config.contains(SemanticsProperties.InvisibleToUser),
            role = config.getOrNull(SemanticsProperties.Role)?.toString(),
            contentDescriptions = descriptions,
            texts = texts,
            testTag = config.getOrNull(SemanticsProperties.TestTag),
        )
    }
}
