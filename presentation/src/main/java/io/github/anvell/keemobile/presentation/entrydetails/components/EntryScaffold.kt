package io.github.anvell.keemobile.presentation.entrydetails.components

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import io.github.anvell.keemobile.presentation.entrydetails.EntryDetailsCommand
import io.github.anvell.keemobile.presentation.entrydetails.EntryDetailsViewState

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EntryScaffold(
    state: EntryDetailsViewState,
    commands: (EntryDetailsCommand) -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    content: @Composable (
        attachmentsSheetState: ModalBottomSheetState,
        historySheetState: ModalBottomSheetState
    ) -> Unit
) {
    val hapticFeedback = LocalHapticFeedback.current
    val attachments = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val history = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

    Scaffold(scaffoldState = scaffoldState) {
        ModalBottomSheetLayout(
            sheetContent = {
                EntryDetailsAttachments(
                    entry = state.entry.unwrap(),
                    binaries = state.activeDatabase()?.database?.meta?.binaries ?: listOf(),
                    saveAttachmentQueue = state.saveAttachmentQueue,
                    savedAttachments = state.savedAttachments,
                    onSaveAttachment = {
                        commands(EntryDetailsCommand.SaveAttachment(it))
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                )
            },
            sheetState = attachments,
            sheetBackgroundColor = MaterialTheme.colors.background,
            scrimColor = Color.Black.copy(alpha = 0.65f)
        ) {
            ModalBottomSheetLayout(
                sheetContent = {
                    EntryDetailsHistory(
                        activeDatabaseId = state.activeDatabaseId,
                        entry = state.entry.unwrap()
                    )
                },
                sheetState = history,
                sheetBackgroundColor = MaterialTheme.colors.background,
                scrimColor = Color.Black.copy(alpha = 0.65f)
            ) {
                content(attachments, history)
            }
        }
    }
}
