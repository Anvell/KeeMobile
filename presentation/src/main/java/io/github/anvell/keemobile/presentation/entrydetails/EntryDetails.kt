package io.github.anvell.keemobile.presentation.entrydetails

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.google.accompanist.insets.statusBarsPadding
import io.github.anvell.async.Fail
import io.github.anvell.async.Success
import io.github.anvell.keemobile.core.ui.components.Spacers
import io.github.anvell.keemobile.core.ui.components.TopAppBar
import io.github.anvell.keemobile.core.ui.locals.LocalAppNavigator
import io.github.anvell.keemobile.core.ui.mappers.ErrorMapper
import io.github.anvell.keemobile.core.ui.mappers.FilterColorMapper
import io.github.anvell.keemobile.core.ui.mappers.IconMapper
import io.github.anvell.keemobile.core.ui.theme.AppTheme
import io.github.anvell.keemobile.presentation.R
import io.github.anvell.keemobile.presentation.data.EntryType
import io.github.anvell.keemobile.presentation.entrydetails.components.EntryDetailsButton
import io.github.anvell.keemobile.presentation.entrydetails.components.EntryDetailsList
import io.github.anvell.keemobile.presentation.entrydetails.components.EntryScaffold
import kotlinx.coroutines.launch

private const val isEditingEnabled = false

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EntryDetails(
    state: EntryDetailsViewState,
    commands: (EntryDetailsCommand) -> Unit
) {
    val context = LocalContext.current
    val navigator = LocalAppNavigator.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    EntryScaffold(
        state = state,
        commands = commands,
        scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState)
    ) { attachments, history ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            AppTheme.TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = { navigator.popBackStack() },
                        modifier = Modifier.padding(start = 5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = MaterialTheme.colors.onBackground
                        )
                    }
                },
                title = {
                    if (state.entry is Success) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(IconMapper.map(state.entry.unwrap().iconId)),
                                contentDescription = null,
                                tint = state.entry.unwrap().backgroundColor?.let {
                                    FilterColorMapper.map(
                                        hexColor = it,
                                        targetColors = AppTheme.colors.filterColors
                                    )
                                } ?: MaterialTheme.colors.onSurface,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacers.S()

                            Text(
                                text = state.entry.unwrap().name,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                actions = {
                    if (isEditingEnabled && state.entryType is EntryType.Actual) {
                        IconButton(
                            onClick = {},
                            modifier = Modifier.padding(end = 5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = MaterialTheme.colors.onBackground
                            )
                        }
                    }
                },
                modifier = Modifier.statusBarsPadding()
            )

            if (state.entry is Success) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .width(dimensionResource(R.dimen.layout_maximum_width))
                        .padding(horizontal = dimensionResource(R.dimen.layout_horizontal_margin))
                        .padding(
                            top = 4.dp,
                            bottom = dimensionResource(R.dimen.layout_vertical_margin)
                        )
                ) {
                    EntryDetailsList(state.entry.unwrap())
                    Spacers.M()

                    if (state.entryType is EntryType.Actual) {
                        if (state.entry.unwrap().attachments.isNotEmpty()) {
                            EntryDetailsButton(
                                text = stringResource(R.string.details_button_files),
                                onClick = { coroutineScope.launch { attachments.show() } }
                            )
                            Spacers.M()
                        }

                        EntryDetailsButton(
                            text = stringResource(R.string.details_button_history),
                            onClick = { coroutineScope.launch { history.show() } }
                        )
                    }

                    Spacer(Modifier.navigationBarsWithImePadding())
                }
            }
        }
    }

    LaunchedEffect(state.entry, state.activeDatabase, state.attachmentStatus) {
        listOf(
            state.entry,
            state.activeDatabase,
            state.attachmentStatus
        ).forEach { item ->
            if (item is Fail && !item.isConsumed) {
                ErrorMapper.map(context, item.error)?.let {
                    snackbarHostState.showSnackbar(message = it)
                }
            }
        }
    }
}
