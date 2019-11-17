package io.github.anvell.keemobile.presentation.explore

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.jakewharton.rxbinding3.widget.textChanges
import io.github.anvell.keemobile.R
import io.github.anvell.keemobile.common.extensions.injector
import io.github.anvell.keemobile.common.mapper.FilterColorMapper
import io.github.anvell.keemobile.common.mapper.IconMapper
import io.github.anvell.keemobile.databinding.FragmentExploreBinding
import io.github.anvell.keemobile.domain.entity.*
import io.github.anvell.keemobile.itemEntry
import io.github.anvell.keemobile.itemHeader
import io.github.anvell.keemobile.itemInfo
import io.github.anvell.keemobile.presentation.base.BaseFragment
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@SuppressLint("ClickableViewAccessibility")
class ExploreFragment : BaseFragment<FragmentExploreBinding>(FragmentExploreBinding::inflate) {

    @Inject
    lateinit var viewModelFactory: ExploreViewModel.Factory

    private val viewModel: ExploreViewModel by fragmentViewModel()

    @Inject
    lateinit var iconMapper: IconMapper

    private lateinit var filterColorMapper: FilterColorMapper

    override fun onAttach(context: Context) {
        requireActivity().injector.inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDrawer()?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        filterColorMapper = FilterColorMapper(
            ContextCompat.getColor(requireContext(), R.color.onSurface),
            resources.getIntArray(R.array.filterColors)
        )

        requireActivity().onBackPressedDispatcher
            .addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() = onBackPressed()
        })

        initSearch()

        binding.exploreRoot.setOnTouchListener { _, _ ->
            hideSoftKeyboard()
            binding.search.clearFocus()
            false
        }

        binding.navigateButton.setOnClickListener {
            updateUiOnNavigation(false)
            withState(viewModel) { state ->
                when {
                    state.searchResults !is Uninitialized -> binding.search.text.clear()
                    state.rootStack.isEmpty() -> {
                        getDrawer()?.openDrawer(GravityCompat.START)
                    }
                    else -> viewModel.navigateUp()
                }
            }
        }
    }

    private fun initSearch() {
        binding.search.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                hideSoftKeyboard()
            }
        }

        binding.search.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                textView.clearFocus()
                true
            } else false
        }

        binding.searchExtraButton.setOnClickListener {
            if (!binding.search.text.isNullOrEmpty()) {
                binding.search.text.clear()
            } else {
                showExplorePopupMenu(it)
            }
        }
    }

    private fun showExplorePopupMenu(view: View) = withState(viewModel) { state ->
        with(PopupMenu(requireActivity(), view, GravityCompat.END)) {

            if (state.appSettings is Success) {
                addViewModeMenuItem(menu, state.appSettings()!!)
            }
            show()
        }
    }

    private fun addViewModeMenuItem(menu: Menu, settings: AppSettings) = withState(viewModel) { state ->
        when (settings.exploreViewMode) {
            ViewMode.TREE -> {
                menu.add(getString(R.string.explore_menu_show_list))
                    .setOnMenuItemClickListener {
                        binding.exploreView.clear()

                        if (state.rootStack.isNotEmpty()) {
                            viewModel.resetRoot()
                            binding.navigateButton.playReverse()
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

        if(binding.search.text.toString().isNotEmpty()) {
            binding.searchExtraButton.rewindToEnd()
        }

        binding.search
            .textChanges()
            .debounce(300, TimeUnit.MILLISECONDS)
            .onErrorReturn { "" }
            .observeOn(rxSchedulers.main())
            .map { it.toString() }
            .subscribe(::onFilterChanged)
            .disposeOnStop()
    }

    private fun onBackPressed() {
        updateUiOnNavigation(false)
        withState(viewModel) { state ->
            when {
                getDrawer()?.isDrawerOpen(GravityCompat.START) ?: false -> getDrawer()?.closeDrawer(GravityCompat.START)
                state.searchResults !is Uninitialized -> binding.search.text.clear()
                state.rootStack.isEmpty() -> findNavController().navigateUp()
                else -> viewModel.navigateUp()
            }
        }
    }

    override fun invalidate(): Unit = withState(viewModel) { state ->

        when {
            state.searchResults is Success -> state.searchResults()?.also {
                renderFilteredEntries(it.filteredEntries)

                binding.navigateButton.isVisible = false
                binding.searchSeparator.isVisible = true
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
                binding.navigateButton.isVisible = true
                binding.searchSeparator.isVisible = false
            }
        }
    }

    private fun renderEntriesAsList(database: KeyDatabase) {
        renderFilteredEntries(database.findEntries { true })
    }

    private fun renderGroupContents(group: KeyGroup) {
        binding.exploreView.withModels {

            group.groups.forEach { entry ->
                itemEntry {
                    id(entry.uuid.toString())
                    title(entry.name)
                    subtitle(entry.notes)
                    iconId(iconMapper.map(entry.iconId))
                    iconTint(filterColorMapper.defaultColor)
                    isSelected(false)
                    clickListener(View.OnClickListener { onGroupClicked(entry.uuid) })
                }
            }

            group.entries.forEach { entry ->
                itemEntry {
                    id(entry.uuid.toString())
                    title(entry.title)
                    subtitle(entry.username)
                    iconId(iconMapper.map(entry.iconId))
                    iconTint(filterColorMapper.map(entry.backgroundColor))
                    isSelected(false)
                    clickListener(View.OnClickListener { })
                }
            }
        }
    }

    private fun renderFilteredEntries(results: List<SearchResult>) {
        binding.exploreView.withModels {

            if (results.isEmpty()) {
                itemInfo {
                    id(0)
                    title(getString(R.string.explore_search_no_results))
                }
            } else {
                results
                    .sortedBy { it.group.name.toLowerCase(Locale.getDefault()) }
                    .forEach { item ->

                        itemHeader {
                            id(item.group.uuid.toString())
                            title(item.group.name)
                        }

                        item.entries
                            .sortedBy { it.title }
                            .forEach { entry ->
                                itemEntry {
                                    id(entry.uuid.toString())
                                    title(entry.title)
                                    subtitle(entry.username)
                                    iconId(iconMapper.map(entry.iconId))
                                    iconTint(filterColorMapper.map(entry.backgroundColor))
                                    isSelected(false)
                                    clickListener(View.OnClickListener { })
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

    private fun updateUiOnNavigation(isForward: Boolean) {
        withState(viewModel) { state ->
            if (isForward) {
                if (state.rootStack.isEmpty()) {
                    binding.navigateButton.play()
                }
            } else {
                if (state.rootStack.size == 1) {
                    binding.navigateButton.playReverse()
                }
            }
        }
    }

    private fun onFilterChanged(filter: String) {
        withState(viewModel) { state ->
            when {
                state.searchResults is Uninitialized && filter.isNotEmpty() -> {
                    binding.searchExtraButton.play()
                    binding.exploreView.clear()
                }
                state.searchResults !is Uninitialized && filter.isEmpty() -> {
                    binding.searchExtraButton.playReverse()
                }
            }

            when {
                filter.isEmpty() -> viewModel.clearFilter()
                filter.isNotBlank() -> viewModel.filterEntries(filter)
            }
        }
    }

}
