package io.github.anvell.keemobile.presentation.explore

import io.github.anvell.keemobile.domain.entity.ViewMode
import java.util.*

sealed class ExploreCommand {
    class ChangeViewMode(val value: ViewMode) : ExploreCommand()

    object NavigateToRoot : ExploreCommand()

    object NavigateUp : ExploreCommand()

    class NavigateToGroup(val id: UUID) : ExploreCommand()

    class UpdateFilter(val value: String) : ExploreCommand()

    object CloseAllFiles : ExploreCommand()
}
