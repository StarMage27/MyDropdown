package io.github.starmage27.mydropdown

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import kotlin.math.roundToInt


@Composable
fun MyDropdown(
    modifier: Modifier = Modifier,
    options: List<String>,
    selected: String,
    onValueChange: (String) -> Unit = { _ -> },
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit = { _ -> },
) {
    val interactionSource = remember { MutableInteractionSource() }
    val density = LocalDensity.current
    val toPX: Dp.() -> Int = { (this.value * density.density).roundToInt() }
    val toDP: Int.() -> Dp = { (this / density.density).dp }

    val minHeight = 36.dp
    val minHeightPx = minHeight.toPX()

    val expandAnimation = animateFloatAsState(
        targetValue = if (expanded) 1f else 0f
    )

    val heightAnimation = animateDpAsState(
        targetValue = if (expanded) minHeight * (options.size + 1) else minHeight,
    )

    val widthPx = remember { mutableIntStateOf(128) }
    val widthAnimationPx = animateIntAsState(targetValue = widthPx.intValue)

    val optionsVisible = expandAnimation.value != 0f

    val dropdownBackground: @Composable (content: @Composable (modifier: Modifier) -> Unit) -> Unit = @Composable
    { content ->
        Surface(
            modifier = modifier
                .widthIn(min = widthAnimationPx.value.toDP() * expandAnimation.value )
                .height(heightAnimation.value)
                .clipToBounds()
                .clip(RoundedCornerShape(8.dp))
                .clickable(
                    enabled = true,
                    interactionSource = interactionSource,
                    indication = ripple(),
                    onClickLabel = "",
                    role = Role.DropdownList,
                    onClick = {
                        onExpandedChange(!expanded)
                    },
                )
            ,
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp,
            shadowElevation = 4.dp,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        ) {
            Column(
                modifier = Modifier
                    .width(IntrinsicSize.Max)
                    .onSizeChanged { newSize ->
                        widthPx.intValue = newSize.width
                    }
                //.wrapContentWidth()
            ) {
                content(
                    Modifier
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                    ,
                )
            }
        }
    }

    val dropdownSelectedValue: @Composable (modifier: Modifier) -> Unit = @Composable { dsModifier ->
        Row(
            modifier = dsModifier
                .height(minHeight)
            ,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                modifier = Modifier.padding(4.dp),
                style = MaterialTheme.typography.bodyMedium,
                text = selected,
            )
            if (optionsVisible) {
                Spacer(
                    Modifier
                        .weight(expandAnimation.value)
                    ,
                )
            }
            Icon(
                modifier = Modifier
                    .rotate(expandAnimation.value * 180)
                    .size(18.dp)
                ,
                painter = painterResource(R.drawable.baseline_arrow_drop_down_24),
                contentDescription = "",
            )
        }
    }

    val dropdownContent = @Composable {
        dropdownBackground { dbModifier ->
            dropdownSelectedValue(dbModifier)

            val scrollableState = rememberScrollableState { it }
            if (optionsVisible) {
                HorizontalDivider()
                Column(
                    modifier = Modifier
                        .scrollable(
                            state = scrollableState,
                            orientation = Orientation.Vertical
                        )
                        //.wrapContentWidth()
                        .width(IntrinsicSize.Max)
//                        .onSizeChanged { newSize ->
//                            widthPx = max(newSize.width, widthPx)
//                        }
                    ,
                ) {
                    options.forEach { option ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(minHeight)
                                .clickable(
                                    enabled = true,
                                    onClickLabel = "selected $option",
                                    role = Role.Button,
                                    onClick = {
                                        onValueChange(option)
                                        onExpandedChange(false)
                                    }
                                )
                            ,
                        ) {
                            Text(
                                modifier = dbModifier.padding(vertical = 2.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                text = option,
                                maxLines = 1,
                                overflow = TextOverflow.Visible
                            )
                        }
                    }
                }
            }
        }
    }

    Layout(
        modifier = modifier,
        content = {
            dropdownBackground { dbModifier ->
                dropdownSelectedValue(dbModifier)
            }
            if (optionsVisible) {
                @Suppress("COMPOSE_APPLIER_CALL_MISMATCH")
                Popup(
                    onDismissRequest = { onExpandedChange(false) },
                    properties = PopupProperties(clippingEnabled = false)
                ) {
                    dropdownContent()
                }
            }
        }
    ) { measurables, constraints ->
        val placeables = measurables.map { it.measure(constraints) }

        layout(
            width = widthAnimationPx.value,
            height = minHeightPx
        ) {
            placeables.forEach { placeable ->
                placeable.placeRelative(0, 0)
            }
        }
    }
}