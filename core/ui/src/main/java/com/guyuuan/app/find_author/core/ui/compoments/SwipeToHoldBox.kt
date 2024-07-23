package com.guyuuan.app.find_author.core.ui.compoments

import androidx.annotation.FloatRange
import androidx.compose.animation.core.SpringSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/**
 * @author: guyuuan
 * @createTime: 7/3/24 17:22
 * @description:
 **/

@Composable
fun SwipeToHoldBox(
    modifier: Modifier = Modifier,
    state: SwipeToHoldBoxState = rememberSwipeToHoldBoxState(),
    @FloatRange(from = 0.0, to = 1.0) endAnchor: Float = 1f,
    enableDismissFromStartToEnd: Boolean = true,
    enableDismissFromEndToStart: Boolean = true,
    orientation: Orientation = Orientation.Vertical,
    backgroundContent: @Composable BoxScope.() -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    Box(
        modifier = modifier.anchoredDraggable(
            state = state.anchoredDraggableState,
            orientation = orientation,
            reverseDirection = isRtl
        )
    ) {
        Box(modifier = Modifier.matchParentSize()) {
            backgroundContent()
        }
        Box(
            modifier = Modifier
                .matchParentSize()
                .swipeToHoldBoxAnchors(
                    state,
                    orientation,
                    endAnchor,
                    enableDismissFromStartToEnd,
                    enableDismissFromEndToStart
                )
        ) {
            content()
        }
    }
}

enum class SwipeToHoldBoxValue {
    StartToEnd, EndToStart, Settled
}

private val DismissThreshold = 125.dp

internal object SwipeToHoldBoxDefaults {
    val positionalThreshold: (totalDistance: Float) -> Float
        @Composable get() = with(LocalDensity.current) {
            { 56.dp.toPx() }
        }
}

@Composable
fun rememberSwipeToHoldBoxState(
    key:String?=null,
    initialValue: SwipeToHoldBoxValue = SwipeToHoldBoxValue.Settled,
    confirmValueChange: (SwipeToHoldBoxValue) -> Boolean = { true },
    positionalThreshold: (totalDistance: Float) -> Float = SwipeToHoldBoxDefaults.positionalThreshold
): SwipeToHoldBoxState {
    val density = LocalDensity.current
    return rememberSaveable(
        key = key,
        saver = SwipeToHoldBoxState.Saver(
            density, confirmValueChange, positionalThreshold
        )
    ) {
        SwipeToHoldBoxState(initialValue, density, confirmValueChange, positionalThreshold)
    }
}

@Stable
class SwipeToHoldBoxState(
    initialValue: SwipeToHoldBoxValue = SwipeToHoldBoxValue.Settled,
    private val density: Density,
    confirmValueChange: (SwipeToHoldBoxValue) -> Boolean,
    positionalThreshold: (totalDistance: Float) -> Float
) {
    internal val anchoredDraggableState = AnchoredDraggableState(
        initialValue = initialValue,
        positionalThreshold = positionalThreshold,
        animationSpec = SpringSpec(),
        confirmValueChange = confirmValueChange,
        velocityThreshold = { with(density) { DismissThreshold.toPx() } },
    )
    val offset get() = anchoredDraggableState.offset

    val currentValue get() = anchoredDraggableState.currentValue

    val targetValue get() = anchoredDraggableState.targetValue

    @get:FloatRange(from = 0.0, to = 1.0)
    val progress get() = anchoredDraggableState.progress

    fun requireOffset(): Float = anchoredDraggableState.requireOffset()

    companion object {
        fun Saver(
            density: Density,
            confirmValueChange: (SwipeToHoldBoxValue) -> Boolean,
            positionalThreshold: (totalDistance: Float) -> Float
        ) = Saver<SwipeToHoldBoxState, SwipeToHoldBoxValue>(save = { it.currentValue },
            restore = { SwipeToHoldBoxState(it, density, confirmValueChange, positionalThreshold) })
    }
}

internal fun Modifier.swipeToHoldBoxAnchors(
    state: SwipeToHoldBoxState,
    orientation: Orientation,
    @FloatRange(from = 0.0, to = 1.0) endAnchor: Float,
    enableDismissFromStartToEnd: Boolean,
    enableDismissFromEndToStart: Boolean
) = this then SwipeToHoldAnchorsElement(
    state, orientation, endAnchor, enableDismissFromStartToEnd, enableDismissFromEndToStart
)

internal class SwipeToHoldAnchorsElement(
    private val state: SwipeToHoldBoxState,
    private val orientation: Orientation,
    @FloatRange(from = 0.0, to = 1.0) private val endAnchor: Float,
    private val enableDismissFromStartToEnd: Boolean,
    private val enableDismissFromEndToStart: Boolean
) : ModifierNodeElement<SwipeToHoldAnchorsNode>() {
    override fun create(): SwipeToHoldAnchorsNode = SwipeToHoldAnchorsNode(
        state, orientation, endAnchor, enableDismissFromStartToEnd, enableDismissFromEndToStart
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other is SwipeToHoldAnchorsNode) {
            if (state != other.state) return false
            if (enableDismissFromStartToEnd != other.enableDismissFromStartToEnd) return false
            if (enableDismissFromEndToStart != other.enableDismissFromEndToStart) return false
            return true
        }
        return false
    }

    override fun hashCode(): Int {
        var result = state.hashCode()
        result = 31 * result + enableDismissFromStartToEnd.hashCode()
        result = 31 * result + enableDismissFromEndToStart.hashCode()
        return result
    }

    override fun update(node: SwipeToHoldAnchorsNode) {
        node.state = state
        node.enableDismissFromStartToEnd = enableDismissFromStartToEnd
        node.enableDismissFromEndToStart = enableDismissFromEndToStart
    }

    override fun InspectorInfo.inspectableProperties() {
        debugInspectorInfo {
            properties["state"] = state
            properties["enableDismissFromStartToEnd"] = enableDismissFromStartToEnd
            properties["enableDismissFromEndToStart"] = enableDismissFromEndToStart
        }
    }
}

internal class SwipeToHoldAnchorsNode(
    var state: SwipeToHoldBoxState,
    private val orientation: Orientation,
    @FloatRange(from = 0.0, to = 1.0) private val endAnchor: Float,
    var enableDismissFromStartToEnd: Boolean,
    var enableDismissFromEndToStart: Boolean,
) : Modifier.Node(), LayoutModifierNode {

    private var didLookahead: Boolean = false

    override fun onDetach() {
        didLookahead = false
    }

    override fun MeasureScope.measure(
        measurable: Measurable, constraints: Constraints
    ): MeasureResult {
        val placeable = measurable.measure(constraints)
        if (isLookingAhead || !didLookahead) {
            val totalDistance = if (orientation == Orientation.Vertical) {
                placeable.height
            } else {
                placeable.width
            }.toFloat() * endAnchor
            val newAnchors = DraggableAnchors {
                SwipeToHoldBoxValue.Settled at 0f
                if (enableDismissFromStartToEnd) {
                    SwipeToHoldBoxValue.StartToEnd at totalDistance
                }
                if (enableDismissFromEndToStart) {
                    SwipeToHoldBoxValue.EndToStart at -totalDistance
                }
            }
            state.anchoredDraggableState.updateAnchors(newAnchors)
        }
        didLookahead = isLookingAhead || didLookahead
        return layout(placeable.width, placeable.height) {
            val offset = if (isLookingAhead) {
                state.anchoredDraggableState.anchors.positionOf(state.targetValue)
            } else state.requireOffset()
            placeable.place(
                x = if (orientation == Orientation.Horizontal) offset.toInt() else 0,
                y = if (orientation == Orientation.Vertical) offset.toInt() else 0
            )
        }
    }

}

@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
@Composable
private fun SwipeToDismissBoxPreview() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        SwipeToHoldBox(modifier = Modifier
            .size(100.dp)
            .clipToBounds(),
            state = rememberSwipeToHoldBoxState(
                positionalThreshold = { 0.1f * it },
            ),
            orientation = Orientation.Vertical,
            endAnchor = 0.6f,
            backgroundContent = {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(color = MaterialTheme.colorScheme.surface)
                )
            }) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.inverseSurface)
            )
        }
    }
}