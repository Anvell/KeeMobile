@file:Suppress("unused", "NOTHING_TO_INLINE")

package io.github.anvell.keemobile.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.isFocused
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.anvell.keemobile.core.ui.R
import io.github.anvell.keemobile.core.ui.theme.AppTheme

private val TextFieldTrailingItemPadding = 4.dp
private val TextFieldHorizontalPadding = 16.dp
private val TextFieldVerticalPadding = 14.dp
private val IconHorizontalPadding = 24.dp

private val textSelectionColors
    @Composable get() = TextSelectionColors(
        handleColor = MaterialTheme.colors.secondary,
        backgroundColor = MaterialTheme.colors.onBackground.copy(alpha = 0.4f)
    )

@Composable
inline fun AppTheme.ClearableTextField(
    value: String,
    noinline onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    noinline hint: @Composable (() -> Unit)? = null,
    textStyle: TextStyle = LocalTextStyle.current,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    noinline onTextFieldFocused: (Boolean) -> Unit = {},
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    AppTheme.TextField(
        value = value,
        onValueChange = onValueChange,
        hint = hint,
        textStyle = textStyle,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        onTextFieldFocused = onTextFieldFocused,
        visualTransformation = visualTransformation,
        trailing = {
            if (value.isNotEmpty()) {
                IconButton(
                    onClick = { onValueChange("") }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = null,
                        tint = MaterialTheme.colors.onSurface,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        },
        modifier = modifier
    )
}

@Composable
inline fun AppTheme.PasswordTextField(
    value: String,
    noinline onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    noinline hint: @Composable (() -> Unit)? = null,
    textStyle: TextStyle = LocalTextStyle.current,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    noinline onTextFieldFocused: (Boolean) -> Unit = {}
) {
    var hidePassword by rememberSaveable { mutableStateOf(true) }

    AppTheme.TextField(
        value = value,
        onValueChange = onValueChange,
        hint = hint,
        textStyle = textStyle,
        keyboardOptions = keyboardOptions.copy(
            keyboardType = KeyboardType.Password
        ),
        keyboardActions = keyboardActions,
        onTextFieldFocused = onTextFieldFocused,
        visualTransformation = if (hidePassword) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        trailing = {
            val icon = painterResource(
                if (hidePassword) {
                    R.drawable.ic_eye_hide
                } else {
                    R.drawable.ic_eye_show
                }
            )
            IconButton(onClick = { hidePassword = !hidePassword }) {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colors.onSurface
                )
            }
        },
        modifier = modifier
    )
}

@Composable
fun AppTheme.TextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    hint: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    textStyle: TextStyle = LocalTextStyle.current,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    onTextFieldFocused: (Boolean) -> Unit = {},
    visualTransformation: VisualTransformation = VisualTransformation.None,
    textColor: Color = MaterialTheme.colors.onSurface,
    cursorColor: Color = MaterialTheme.colors.secondary,
    backgroundColor: Color = MaterialTheme.colors.surface,
    shape: Shape = MaterialTheme.shapes.medium,
    horizontalPadding: Dp = TextFieldHorizontalPadding,
    singleLine: Boolean = true
) {
    var lastFocusState by remember { mutableStateOf(FocusState.Inactive) }
    val color = textColor.takeOrElse {
        textStyle.color.takeOrElse { LocalContentColor.current }
    }
    val mergedStyle = textStyle.merge(TextStyle(color = color))

    BasicTextField(
        value = value,
        onValueChange = { onValueChange(it) },
        modifier = modifier.onFocusEvent { state ->
            if (lastFocusState != state) {
                onTextFieldFocused(state == FocusState.Active)
            }
            lastFocusState = state
        },
        textStyle = mergedStyle,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        visualTransformation = visualTransformation,
        cursorBrush = SolidColor(cursorColor)
    ) { innerTextField ->
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor, shape)
        ) {
            CompositionLocalProvider(LocalTextSelectionColors provides textSelectionColors) {
                Box(
                    Modifier
                        .padding(vertical = TextFieldVerticalPadding)
                        .padding(
                            start = horizontalPadding,
                            end = trailing?.let { TextFieldTrailingItemPadding }
                                ?: horizontalPadding
                        ).weight(1f)
                ) {
                    innerTextField()

                    if (value.isEmpty() &&
                        !lastFocusState.isFocused &&
                        hint != null
                    ) {
                        ProvideTextStyle(
                            MaterialTheme.typography.body1.copy(
                                color = LocalTextStyle.current.color.copy(
                                    alpha = ContentAlpha.medium
                                )
                            )
                        ) {
                            hint()
                        }
                    }
                }
            }

            trailing?.let { content ->
                Box(
                    Modifier
                        .padding(end = TextFieldTrailingItemPadding)
                        .wrapContentWidth()
                ) {
                    content()
                }
            }
        }
    }
}
