package io.github.anvell.keemobile.presentation.drawer

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavOptions
import dagger.hilt.android.AndroidEntryPoint
import io.github.anvell.keemobile.core.constants.Args
import io.github.anvell.keemobile.core.ui.ativities.NavControllerHolder
import io.github.anvell.keemobile.core.ui.fragments.ViewBindingFragment
import io.github.anvell.keemobile.core.ui.mvi.MviView
import io.github.anvell.keemobile.domain.alias.VaultId
import io.github.anvell.keemobile.domain.datatypes.Success
import io.github.anvell.keemobile.presentation.R
import io.github.anvell.keemobile.presentation.databinding.FragmentDrawerBinding
import io.github.anvell.keemobile.presentation.drawer
import io.github.anvell.keemobile.presentation.explore.ExploreArgs
import io.github.anvell.keemobile.presentation.header
import io.github.anvell.keemobile.presentation.home.HomeViewModel
import io.github.anvell.keemobile.presentation.home.HomeViewState

@AndroidEntryPoint
class DrawerFragment : ViewBindingFragment<FragmentDrawerBinding>(FragmentDrawerBinding::inflate),
    MviView<HomeViewModel, HomeViewState> {
    override val viewModel: HomeViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.selectSubscribe(HomeViewState::activeDatabaseId)
            .observe(viewLifecycleOwner) {
                if (it is Success && !it.isConsumed) {
                    navigateOnDatabaseSwitch(it())
                }
            }
        stateSubscribe(viewLifecycleOwner)
    }

    override fun render(state: HomeViewState) {
        requireBinding().drawerMenu.withModels {
            header {
                val title = getString(R.string.drawer_file_list_header)
                id(TAG + title)
                title(title)
            }

            if (state.openDatabases.isNotEmpty()) {
                state.openDatabases.forEach { entry ->
                    drawer {
                        id(entry.id)
                        title(entry.source.name)
                        icon(R.drawable.ic_close)
                        iconContentDescription(getString(R.string.drawer_content_description_close, entry.source.name))
                        iconClickListener(View.OnClickListener { viewModel.closeDatabase(entry.id) })

                        if (entry.id == state.activeDatabaseId()) {
                            isSelected(true)
                        } else {
                            clickListener(View.OnClickListener {
                                getDrawer()?.closeDrawer(GravityCompat.START)
                                viewModel.switchDatabase(entry.id)
                            })
                        }
                    }
                }
            }

            drawer {
                val title = getString(R.string.drawer_file_list_open)
                id(TAG + title)
                title(title)
                clickListener(View.OnClickListener {
                    (activity as? NavControllerHolder)
                        ?.getNavController()
                        ?.navigate(R.id.action_open_database)
                })
            }

            header {
                val title = getString(R.string.drawer_app_header)
                id(TAG + title)
                title(title)
            }

            drawer {
                val title = getString(R.string.drawer_app_settings)
                id(TAG + title)
                title(title)
                clickListener(View.OnClickListener { })
            }

            drawer {
                val title = getString(R.string.drawer_app_about)
                id(TAG + title)
                title(title)
                clickListener(View.OnClickListener { })
            }
        }
    }

    private fun navigateOnDatabaseSwitch(activeDatabaseId: VaultId) {
        if (activeDatabaseId.isNotBlank()) {
            (activity as? NavControllerHolder)
                ?.getNavController()
                ?.navigate(R.id.exploreFragment, bundleOf(Args.KEY to ExploreArgs(activeDatabaseId)),
                    NavOptions.Builder().setPopUpTo(R.id.app_navigation, false).build()
                )
        } else {
            (activity as? NavControllerHolder)
                ?.getNavController()
                ?.navigate(R.id.openFragment, null,
                    NavOptions.Builder().setPopUpTo(R.id.app_navigation, false).build()
                )
        }
    }

    companion object {
        private val TAG = this::class.simpleName
    }
}
