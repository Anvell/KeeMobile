package io.github.anvell.keemobile.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.anvell.keemobile.core.ui.theme.AppTheme
import kotlin.math.max

private val AppBarHeight = 56.dp
private val TitleTextSize = 18.sp
private val TitlePadding = 8.dp

@Composable
fun AppTheme.TopAppBar(
    modifier: Modifier = Modifier,
    navigationIcon: @Composable BoxScope.() -> Unit = {},
    title: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    backgroundColor: Color = MaterialTheme.colors.background,
) {
    TopAppBarLayout(
        modifier = modifier
            .background(backgroundColor)
            .height(AppBarHeight)
            .fillMaxWidth()
    ) {
        Box(
            contentAlignment = Alignment.CenterStart,
            content = navigationIcon
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = TitlePadding)
        ) {
            ProvideTextStyle(
                value = MaterialTheme.typography.body1.copy(
                    color = MaterialTheme.colors.onSecondary,
                    fontSize = TitleTextSize
                ),
                content = title
            )
        }

        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            content = actions
        )
    }
}

private const val Start = 0
private const val Middle = 1
private const val End = 2

@Composable
private fun TopAppBarLayout(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        require(measurables.size == 3) { "Only 3 slots are supported for layout." }

        val startWidth = measurables[Start].maxIntrinsicWidth(constraints.maxHeight)
        val endWidth = measurables[End].maxIntrinsicWidth(constraints.maxHeight)
        val middleWidth = constraints.maxWidth - (max(startWidth, endWidth) * 2)

        val start = measurables[Start].measure(constraints)
        val end = measurables[End].measure(constraints)
        val middle = measurables[Middle].measure(
            constraints.copy(minWidth = middleWidth, maxWidth = middleWidth)
        )

        layout(constraints.maxWidth, constraints.maxHeight) {
            start.place(x = 0, y = 0)
            middle.place(x = (constraints.maxWidth / 2) - (middleWidth / 2), y = 0)
            end.place(x = constraints.maxWidth - endWidth, y = 0)
        }
    }
}
