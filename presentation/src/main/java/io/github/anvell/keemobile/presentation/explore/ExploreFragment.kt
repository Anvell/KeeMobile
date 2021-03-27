package io.github.anvell.keemobile.presentation.explore

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.jakewharton.rxbinding3.widget.textChanges
import dagger.hilt.android.AndroidEntryPoint
import io.github.anvell.keemobile.core.constants.Args
import io.github.anvell.keemobile.core.ui.extensions.getColorFromAttr
import io.github.anvell.keemobile.core.ui.extensions.hideSoftKeyboard
import io.github.anvell.keemobile.core.ui.extensions.toast
import io.github.anvell.keemobile.core.ui.fragments.ViewBindingFragment
import io.github.anvell.keemobile.core.ui.mappers.FilterColorMapper
import io.github.anvell.keemobile.core.ui.mappers.IconMapper
import io.github.anvell.keemobile.core.ui.mvi.MviRxView
import io.github.anvell.keemobile.domain.datatypes.Success
import io.github.anvell.keemobile.domain.datatypes.Uninitialized
import io.github.anvell.keemobile.domain.entity.*
import io.github.anvell.keemobile.presentation.R
import io.github.anvell.keemobile.presentation.databinding.FragmentExploreBinding
import io.github.anvell.keemobile.presentation.entry
import io.github.anvell.keemobile.presentation.entry.EntryDetailsArgs
import io.github.anvell.keemobile.presentation.header
import io.github.anvell.keemobile.presentation.info
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
@SuppressLint("ClickableViewAccessibility")
class ExploreFragment : ViewBindingFragment<FragmentExploreBinding>(
    FragmentExploreBinding::inflate
), MviRxView<ExploreViewModel, ExploreViewState> {
    override val viewModel: ExploreViewModel by viewModels()
    private lateinit var filterColorMapper: FilterColorMapper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDrawer()?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        filterColorMapper = FilterColorMapper(
            requireContext().getColorFromAttr(R.attr.colorOnSurface),
            resources.getIntArray(R.array.filterColors)
        )

        requireActivity().onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    onBackPressed()
                }
        })

        initSearch()

        requireBinding().exploreRoot.setOnTouchListener { _, _ ->
            hideSoftKeyboard()
            requireBinding().search.clearFocus()
            false
        }

        requireBinding().navigateButton.setOnClickListener {
            updateUiOnNavigation(false)
            viewModel.withState() { state ->
                when {
                    state.searchResults !is Uninitialized -> requireBinding().search.text.clear()
                    state.rootStack.isEmpty() -> {
                        getDrawer()?.openDrawer(GravityCompat.START)
                    }
                    else -> viewModel.navigateUp()
                }
            }
        }
        stateSubscribe(viewLifecycleOwner)
    }

    private fun initSearch() = with(requireBinding()) {
        search.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                hideSoftKeyboard()
            }
        }

        search.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                textView.clearFocus()
                true
            } else false
        }

        searchExtraButton.setOnClickListener {
            if (!search.text.isNullOrEmpty()) {
                search.text.clear()
            } else {
                showExplorePopupMenu(it)
            }
        }
    }

    private fun showExplorePopupMenu(view: View) = viewModel.withState() { state ->
        with(PopupMenu(requireActivity(), view, GravityCompat.END)) {
            if (state.appSettings is Success) {
                addViewModeMenuItem(menu, state.appSettings()!!)
            }
            show()
        }
    }

    private fun addViewModeMenuItem(menu: Menu, settings: AppSettings) = viewModel.withState() { state ->
        when (settings.exploreViewMode) {
            ViewMode.TREE -> {
                menu.add(getString(R.string.explore_menu_show_list))
                    .setOnMenuItemClickListener {
                        requireBinding().exploreView.clear()

                        if (state.rootStack.isNotEmpty()) {
                            viewModel.resetRoot()
                            requireBinding().navigateButton.playReverse()
                        }
                        viewModel.updateAppSettings(settings.copy(exploreViewMode = ViewMode.LIST))
                        true
                    }
            }
            ViewMode.LIST -> {
                menu.add(getString(R.string.explore_menu_show_folders))
                    .setOnMenuItemClickListener {
                        viewModel.updateAppSettings(settings.copy(exploreViewMode = ViewMode.TREE))
                        true
                    }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        if (requireBinding().search.text.toString().isNotEmpty()) {
            requireBinding().searchExtraButton.rewindToEnd()
        }

        requireBinding().search
            .textChanges()
            .debounce(300, TimeUnit.MILLISECONDS)
            .onErrorReturn { "" }
            .observeOn(rxSchedulers.main())
            .map { it.toString() }
            .subscribe(::onFilterChanged)
            .disposeOnStop()
    }

    private fun onBackPressed() = viewModel.withState() { state ->
        updateUiOnNavigation(false)
        when {
            getDrawer()?.isDrawerOpen(GravityCompat.START) ?: false -> getDrawer()?.closeDrawer(
                GravityCompat.START
            )
            state.searchResults !is Uninitialized -> requireBinding().search.text.clear()
            state.rootStack.isEmpty() -> {
                viewModel.closeAll()
                toast(getString(R.string.explore_all_files_closed))
                findNavController().navigateUp()
            }
            else -> viewModel.navigateUp()
        }
    }

    override fun render(state: ExploreViewState) {
        when {
            state.searchResults is Success -> state.searchResults()?.also {
                renderFilteredEntries(it.filteredEntries)
                requireBinding().navigateButton.isVisible = false
                requireBinding().searchSeparator.isVisible = true
            }
            state.searchResults is Uninitialized && state.activeDatabase is Success -> {
                state.activeDatabase()?.database?.let { db ->
                    when (state.appSettings()?.exploreViewMode) {
                        ViewMode.TREE -> {
                            val group = if (state.rootStack.isEmpty()) db.root else {
                                db.findGroup { it.uuid == state.rootStack.last() }
                            }
                            renderGroupContents(group!!)
                        }
                        ViewMode.LIST -> {
                            renderEntriesAsList(db)
                        }
                    }

                }
                requireBinding().navigateButton.isVisible = true
                requireBinding().searchSeparator.isVisible = false
            }
        }
    }

    private fun renderEntriesAsList(database: KeyDatabase) {
        renderFilteredEntries(database.findEntries { true })
    }

    private fun renderGroupContents(group: KeyGroup) {
        requireBinding().exploreView.withModels {
            group.groups.forEach { item ->
                entry {
                    id(item.uuid.toString())
                    title(item.name)
                    subtitle(item.notes)
                    iconId(IconMapper.map(item.iconId))
                    iconTint(filterColorMapper.defaultColor)
                    isSelected(false)
                    clickListener(View.OnClickListener { onGroupClicked(item.uuid) })
                }
            }

            group.entries.forEach { item ->
                entry {
                    id(item.uuid.toString())
                    title(item.name)
                    subtitle(item.username)
                    iconId(IconMapper.map(item.iconId))
                    iconTint(filterColorMapper.map(item.backgroundColor))
                    isSelected(false)
                    clickListener(View.OnClickListener { onEntryClicked(item.uuid) })
                }
            }
        }
    }

    private fun renderFilteredEntries(results: List<SearchResult>) {
        requireBinding().exploreView.withModels {
            if (results.isEmpty()) {
                info {
                    id(0)
                    title(getString(R.string.explore_search_no_results))
                }
            } else {
                results
                    .sortedBy { it.group.name.toLowerCase(Locale.getDefault()) }
                    .forEach { searchResult ->
                        header {
                            id(searchResult.group.uuid.toString())
                            title(searchResult.group.name)
                        }

                        searchResult.entries
                            .sortedBy { it.name }
                            .forEach { item ->
                                entry {
                                    id(item.uuid.toString())
                                    title(item.name)
                                    subtitle(item.username)
                                    iconId(IconMapper.map(item.iconId))
                                    iconTint(filterColorMapper.map(item.backgroundColor))
                                    isSelected(false)
                                    clickListener(View.OnClickListener { onEntryClicked(item.uuid) })
                                }
                            }
                    }
            }
        }
    }

    private fun onGroupClicked(id: UUID) {
        updateUiOnNavigation(true)
        viewModel.activateGroup(id)
    }

    private fun onEntryClicked(id: UUID) = viewModel.withState() { state ->
        findNavController().navigate(
            R.id.action_entry_details,
            bundleOf(Args.Key to EntryDetailsArgs(state.activeDatabaseId, id))
        )
    }

    private fun updateUiOnNavigation(isForward: Boolean) = viewModel.withState() { state ->
        if (isForward) {
            if (state.rootStack.isEmpty()) {
                requireBinding().navigateButton.play()
            }
        } else {
            if (state.rootStack.size == 1) {
                requireBinding().navigateButton.playReverse()
            }
        }
    }

    private fun onFilterChanged(filter: String) = viewModel.withState() { state ->
        when {
            state.searchResults is Uninitialized && filter.isNotEmpty() -> {
                requireBinding().searchExtraButton.play()
                requireBinding().exploreView.clear()
            }
            state.searchResults !is Uninitialized && filter.isEmpty() -> {
                requireBinding().searchExtraButton.playReverse()
            }
        }

        when {
            filter.isEmpty() -> viewModel.clearFilter()
            filter.isNotBlank() -> viewModel.filterEntries(filter)
        }
    }
}
