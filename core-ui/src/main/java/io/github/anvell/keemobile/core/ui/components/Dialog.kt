package io.github.anvell.keemobile.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.anvell.keemobile.core.ui.R
import io.github.anvell.keemobile.core.ui.theme.AppTheme

data class OptionDialogItem(
    val title: String,
    val action: () -> Unit,
)

@Composable
fun AppTheme.OptionDialog(
    items: List<OptionDialogItem>,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppTheme.Dialog(
        onDismissRequest = onDismissRequest,
        buttons = {
            Column {
                items.forEachIndexed { i, item ->
                    Row(
                        modifier = Modifier
                            .clickable {
                                onDismissRequest()
                                item.action()
                            }
                            .fillMaxWidth()
                            .padding(dimensionResource(R.dimen.list_item_vertical_padding))
                    ) {
                        Text(item.title, color = MaterialTheme.colors.onSurface)
                    }
                    if (i < items.lastIndex) Divider()
                }
            }
        },
        modifier = modifier
    )
}

@Composable
fun AppTheme.Dialog(
    onDismissRequest: () -> Unit,
    buttons: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    title: (@Composable () -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    backgroundColor: Color = MaterialTheme.colors.surface,
    contentColor: Color = contentColorFor(backgroundColor),
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            color = backgroundColor,
            contentColor = contentColor,
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier.verticalScroll(rememberScrollState())
            ) {
                if (title != null) {
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                        val textStyle = MaterialTheme.typography.subtitle1.copy(
                            textAlign = TextAlign.Center
                        )
                        ProvideTextStyle(textStyle, title)
                    }
                    Spacers.M()
                }

                if (text != null) {
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                        val textStyle = MaterialTheme.typography.body1.copy(
                            textAlign = TextAlign.Center
                        )
                        ProvideTextStyle(textStyle, text)
                    }
                    Spacer(Modifier.height(28.dp))
                }
                buttons()
            }
        }
    }
}
