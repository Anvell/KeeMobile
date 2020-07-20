package io.github.anvell.keemobile.presentation.drawer

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.navigation.NavOptions
import dagger.hilt.android.AndroidEntryPoint
import io.github.anvell.keemobile.R
import io.github.anvell.keemobile.common.constants.Args
import io.github.anvell.keemobile.databinding.FragmentDrawerBinding
import io.github.anvell.keemobile.domain.alias.VaultId
import io.github.anvell.keemobile.domain.entity.Success
import io.github.anvell.keemobile.itemDrawer
import io.github.anvell.keemobile.itemHeader
import io.github.anvell.keemobile.presentation.base.MviView
import io.github.anvell.keemobile.presentation.base.ViewBindingFragment
import io.github.anvell.keemobile.presentation.explore.ExploreArgs
import io.github.anvell.keemobile.presentation.home.HomeViewModel
import io.github.anvell.keemobile.presentation.home.HomeViewState
import io.github.anvell.keemobile.presentation.home.NavControllerHolder

@AndroidEntryPoint
class DrawerFragment : ViewBindingFragment<FragmentDrawerBinding>(R.layout.fragment_drawer),
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
            itemHeader {
                val title = getString(R.string.drawer_file_list_header)
                id(TAG + title)
                title(title)
            }

            if (state.openDatabases.isNotEmpty()) {
                state.openDatabases.forEach { entry ->
                    itemDrawer {
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

            itemDrawer {
                val title = getString(R.string.drawer_file_list_open)
                id(TAG + title)
                title(title)
                clickListener(View.OnClickListener {
                    (activity as? NavControllerHolder)
                        ?.getNavController()
                        ?.navigate(R.id.action_open_database)
                })
            }

            itemHeader {
                val title = getString(R.string.drawer_app_header)
                id(TAG + title)
                title(title)
            }

            itemDrawer {
                val title = getString(R.string.drawer_app_settings)
                id(TAG + title)
                title(title)
                clickListener(View.OnClickListener { })
            }

            itemDrawer {
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
