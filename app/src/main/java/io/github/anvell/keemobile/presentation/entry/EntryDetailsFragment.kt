package io.github.anvell.keemobile.presentation.entry

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyController
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.google.android.material.tabs.TabLayoutMediator
import io.github.anvell.keemobile.*
import io.github.anvell.keemobile.common.authentication.OneTimePassword
import io.github.anvell.keemobile.common.extensions.clipToCornerRadius
import io.github.anvell.keemobile.common.extensions.formatAsDateTime
import io.github.anvell.keemobile.common.extensions.injector
import io.github.anvell.keemobile.common.extensions.toast
import io.github.anvell.keemobile.common.mapper.FilterColorMapper
import io.github.anvell.keemobile.common.mapper.IconMapper
import io.github.anvell.keemobile.common.permissions.PermissionsProvider
import io.github.anvell.keemobile.databinding.FragmentEntryDetailsBinding
import io.github.anvell.keemobile.domain.entity.KeyAttachment
import io.github.anvell.keemobile.domain.entity.KeyEntry
import io.github.anvell.keemobile.presentation.base.BaseEpoxyAdapter
import io.github.anvell.keemobile.presentation.base.BaseFragment
import io.github.anvell.keemobile.presentation.widgets.DividerDecoration
import timber.log.Timber
import java.net.URISyntaxException
import javax.inject.Inject

@SuppressLint("ClickableViewAccessibility")
class EntryDetailsFragment :
    BaseFragment<FragmentEntryDetailsBinding>(FragmentEntryDetailsBinding::inflate) {

    @Inject
    lateinit var viewModelFactory: EntryDetailsViewModel.Factory

    private val viewModel: EntryDetailsViewModel by fragmentViewModel()

    @Inject
    lateinit var iconMapper: IconMapper

    @Inject
    lateinit var permissionsProvider: PermissionsProvider

    private lateinit var filterColorMapper: FilterColorMapper

    private lateinit var pagesAdapter: BaseEpoxyAdapter

    override fun onAttach(context: Context) {
        requireActivity().injector.inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDrawer()?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        filterColorMapper = FilterColorMapper(
            ContextCompat.getColor(requireContext(), R.color.onSurface),
            resources.getIntArray(R.array.filterColors)
        )
        binding.navigateButton.setOnClickListener { findNavController().navigateUp() }

        initTabs()
    }

    private fun initTabs() {
        binding.tabs.clipToCornerRadius(resources.getDimension(R.dimen.surface_corner_radius))
        pagesAdapter = BaseEpoxyAdapter(R.layout.item_details_page, R.id.details_page) {
            clipToCornerRadius(resources.getDimension(R.dimen.surface_corner_radius))
            addItemDecoration(
                DividerDecoration(context, R.drawable.list_divider, RecyclerView.VERTICAL)
            )
        }
        binding.pager.adapter = pagesAdapter

        TabLayoutMediator(binding.tabs, binding.pager,
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                tab.setCustomView(R.layout.item_details_tab)
                tab.text = resources.getStringArray(R.array.details_tabs)[position]
            }).attach()

        binding.pager.forEach {
            (it as? RecyclerView)?.apply {
                overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            }
        }
    }

    override fun invalidate(): Unit = withState(viewModel) { state ->
        if (state.entry is Success) {
            state.entry()?.also {
                pagesAdapter.updateModels(
                    listOf(
                        buildGeneralTab(it),
                        buildFilesTab(it, state),
                        buildHistoryTab(it)
                    )
                )
            }
        }
    }

    private fun buildGeneralTab(entry: KeyEntry): EpoxyController.() -> Unit = {
        itemDetailsHeader {
            id("$ID_PROPERTY:${R.string.details_title}")
            title(entry.title)
            iconId(iconMapper.map(entry.iconId))
            iconTint(filterColorMapper.map(entry.backgroundColor))
        }

        buildProperty(R.string.details_username, entry.username)
        buildProperty(R.string.details_password, entry.password, true)
        buildProperty(R.string.details_website, entry.url)
        buildProperty(R.string.details_notes, entry.notes)

        try {
            OneTimePassword.from(entry)?.let {
                itemDetailsOtp {
                    id(R.string.details_otp)
                    title(getString(R.string.details_otp))
                    otp(it)
                    longClickListener { _ -> copyToClipboard(getString(R.string.details_otp), it.calculate()) }
                }
            }
        } catch (e: URISyntaxException) {
            Timber.d(e, "Malformed OTP property.")
        }

        entry.customProperties
            .filter { p -> !OTP_PROPERTIES.any { it.equals(p.key, true) } }
            .forEachIndexed { i, p ->
                buildProperty("$ID_CUSTOM_PROPERTY:$i", p.key, p.value, p.isProtected)
            }

        entry.times?.apply {
            if (expires && expiryTime != null) {
                buildProperty(R.string.details_expires, expiryTime.time.formatAsDateTime())
            }
        }

        if (entry.tags.isNotEmpty()) {
            itemDetailsTags {
                id("$ID_PROPERTY:${R.string.details_tags}")
                title(getString(R.string.details_tags))
                tags(entry.tags)
            }
        }
    }

    private fun buildFilesTab(entry: KeyEntry, state: EntryDetailsViewState): EpoxyController.() -> Unit = {
        if (entry.attachments.size < 1) {
            itemInfo {
                id(ID_DOWNLOADS)
                title(getString(R.string.details_downloads_empty))
            }
        } else {
            state.activeDatabase()?.database?.meta?.binaries?.let { binaries ->
                entry.attachments.forEach { item ->
                    val isProcessing = state.saveAttachmentQueue.contains(item.ref)

                    itemDetailsAttachment {
                        id(item.ref)
                        title(item.key)
                        subtitle(getString(R.string.details_downloads_size, binaries[item.ref].data.size / 1024f))
                        isClickable(!isProcessing)
                        isProcessing(isProcessing)
                        clickListener(View.OnClickListener { saveAttachment(item) })
                    }
                }
            }
        }
    }

    private fun buildHistoryTab(entry: KeyEntry): EpoxyController.() -> Unit = {
        //TODO: Implement builder
    }

    private fun EpoxyController.buildProperty(
        @StringRes titleId: Int, content: String?,
        isMasked: Boolean = false
    ) = buildProperty("$ID_PROPERTY:$titleId", getString(titleId), content, isMasked)

    private fun EpoxyController.buildProperty(
        id: String, title: String, content: String?,
        isMasked: Boolean = false
    ) {
        if (content != null && content.isNotBlank()) {
            if (isMasked) {
                itemDetailsPropertyMasked {
                    id(id)
                    title(title)
                    subtitle(content)
                    longClickListener { _ -> copyToClipboard(title, content) }
                }
            } else {
                itemDetailsProperty {
                    id(id)
                    title(title)
                    subtitle(content)
                    longClickListener { _ -> copyToClipboard(title, content) }
                }
            }
        }
    }

    private fun copyToClipboard(
        label: String,
        content: String,
        notify: Boolean = true
    ): Boolean {
        val copied = clipboardProvider.putText(label, content)

        if (copied && notify) {
            toast(getString(R.string.details_copy_notice, label))
        }

        return copied
    }

    private fun saveAttachment(attachment: KeyAttachment) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ||
            Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            viewModel.saveAttachmentFile(attachment)
        } else {
            permissionsProvider
                .requestPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe({ permissions ->
                    if (permissions.all { p -> p.status == PERMISSION_GRANTED }) {
                        viewModel.saveAttachmentFile(attachment)
                    }
                }, Timber::d)
                .disposeOnStop()
        }
    }

    companion object {
        private const val ID_PROPERTY = "PROPERTY"
        private const val ID_CUSTOM_PROPERTY = "CUSTOM_PROPERTY"
        private const val ID_DOWNLOADS = "DOWNLOADS"

        private val OTP_PROPERTIES = listOf("otp", "TOTP Seed", "TOTP Settings")
    }

}
