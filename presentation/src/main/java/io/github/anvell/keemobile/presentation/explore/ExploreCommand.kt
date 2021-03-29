package io.github.anvell.keemobile.presentation.explore

import io.github.anvell.keemobile.domain.alias.VaultId
import io.github.anvell.keemobile.domain.entity.FileSecrets
import io.github.anvell.keemobile.domain.entity.FileSource
import io.github.anvell.keemobile.domain.entity.ViewMode
import java.util.*

sealed class ExploreCommand {
    class SetActiveDatabase(val value: VaultId) : ExploreCommand()

    class SetEncryptedSecrets(
        val source: FileSource,
        val secrets: FileSecrets
    ) : ExploreCommand()

    class ChangeViewMode(val value: ViewMode) : ExploreCommand()

    object NavigateToRoot : ExploreCommand()

    object NavigateUp : ExploreCommand()

    class NavigateToGroup(val id: UUID) : ExploreCommand()

    class UpdateFilter(val value: String) : ExploreCommand()

    class CloseDatabase(val value: VaultId) : ExploreCommand()

    object CloseAllFiles : ExploreCommand()
}
