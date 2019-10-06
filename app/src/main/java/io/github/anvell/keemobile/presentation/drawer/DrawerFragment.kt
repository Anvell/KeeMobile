package io.github.anvell.keemobile.presentation.drawer

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.navigation.NavOptions
import com.airbnb.mvrx.MvRx
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import io.github.anvell.keemobile.R
import io.github.anvell.keemobile.common.extensions.injector
import io.github.anvell.keemobile.databinding.FragmentDrawerBinding
import io.github.anvell.keemobile.domain.alias.VaultId
import io.github.anvell.keemobile.itemDrawer
import io.github.anvell.keemobile.itemHeader
import io.github.anvell.keemobile.presentation.base.BaseFragment
import io.github.anvell.keemobile.presentation.explore.ExploreArgs
import io.github.anvell.keemobile.presentation.home.HomeViewModel
import io.github.anvell.keemobile.presentation.home.HomeViewState
import io.github.anvell.keemobile.presentation.home.NavControllerHolder
import timber.log.Timber

class DrawerFragment : BaseFragment<FragmentDrawerBinding>(FragmentDrawerBinding::inflate) {

    private val viewModel: HomeViewModel by activityViewModel()

    override fun onAttach(context: Context) {
        requireActivity().injector.inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.asyncSubscribe(HomeViewState::activeDatabaseId,
            onSuccess = ::navigateOnDatabaseSwitch,
            onFail = Timber::d,
            deliveryMode = uniqueOnly()
        )
    }

    override fun onBackPressed() {
        if(getDrawer()?.isDrawerOpen(GravityCompat.START) == true) {
            getDrawer()?.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun invalidate(): Unit = withState(viewModel) { state ->
        binding.drawerMenu.withModels {

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
                        icon(R.drawable.ic_close_square_outline)
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
                ?.navigate(R.id.exploreFragment, bundleOf(MvRx.KEY_ARG to ExploreArgs(activeDatabaseId)),
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
