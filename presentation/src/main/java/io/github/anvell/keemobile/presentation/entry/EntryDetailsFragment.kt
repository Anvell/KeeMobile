package io.github.anvell.keemobile.presentation.entry

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.core.view.forEach
import androidx.core.widget.ImageViewCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.airbnb.epoxy.EpoxyController
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import io.github.anvell.keemobile.core.authentication.OneTimePassword
import io.github.anvell.keemobile.core.constants.Args
import io.github.anvell.keemobile.core.extensions.formatAsDateTime
import io.github.anvell.keemobile.core.extensions.getMimeTypeFromFileName
import io.github.anvell.keemobile.core.permissions.PermissionsProvider
import io.github.anvell.keemobile.core.ui.adapters.BaseEpoxyAdapter
import io.github.anvell.keemobile.core.ui.extensions.clipToCornerRadius
import io.github.anvell.keemobile.core.ui.extensions.getColorFromAttr
import io.github.anvell.keemobile.core.ui.extensions.snackbar
import io.github.anvell.keemobile.core.ui.extensions.toast
import io.github.anvell.keemobile.core.ui.fragments.ViewBindingFragment
import io.github.anvell.keemobile.core.ui.mappers.FilterColorMapper
import io.github.anvell.keemobile.core.ui.mappers.IconMapper
import io.github.anvell.keemobile.core.ui.mvi.MviView
import io.github.anvell.keemobile.core.ui.widgets.DividerDecoration
import io.github.anvell.keemobile.domain.datatypes.Fail
import io.github.anvell.keemobile.domain.datatypes.Success
import io.github.anvell.keemobile.domain.entity.KeyAttachment
import io.github.anvell.keemobile.domain.entity.KeyEntry
import io.github.anvell.keemobile.presentation.*
import io.github.anvell.keemobile.presentation.databinding.FragmentEntryDetailsBinding
import timber.log.Timber
import java.net.URISyntaxException
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
@SuppressLint("ClickableViewAccessibility")
class EntryDetailsFragment : ViewBindingFragment<FragmentEntryDetailsBinding>(
    FragmentEntryDetailsBinding::inflate
), MviView<EntryDetailsViewModel, EntryDetailsViewState> {
    override val viewModel: EntryDetailsViewModel by viewModels()
    private lateinit var filterColorMapper: FilterColorMapper
    private lateinit var pagesAdapter: BaseEpoxyAdapter

    private var currentPage: Int by stateProperty(PAGES_FIRST)

    @Inject
    lateinit var iconMapper: IconMapper

    @Inject
    lateinit var permissionsProvider: PermissionsProvider

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDrawer()?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        filterColorMapper = FilterColorMapper(
            requireContext().getColorFromAttr(R.attr.colorOnSurface),
            resources.getIntArray(R.array.filterColors)
        )
        requireBinding().navigateButton.setOnClickListener { findNavController().navigateUp() }

        initTabs()
        initErrorObservers()
        stateSubscribe(viewLifecycleOwner)
    }

    private fun initTabs() = with(requireBinding()) {
        tabs.clipToCornerRadius(resources.getDimension(R.dimen.surface_corner_radius))
        pagesAdapter = BaseEpoxyAdapter(R.layout.item_details_page, R.id.details_page) {
            clipToCornerRadius(resources.getDimension(R.dimen.surface_corner_radius))
            addItemDecoration(
                DividerDecoration(context, R.drawable.list_divider, RecyclerView.VERTICAL)
            )
        }
        pager.adapter = pagesAdapter

        TabLayoutMediator(tabs, pager,
            TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                tab.setCustomView(R.layout.item_details_tab)
                tab.text = resources.getStringArray(R.array.details_tabs)[position]
            }).attach()

        pager.forEach {
            (it as? RecyclerView)?.apply {
                overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            }
        }

        pager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    currentPage = position
                }
            }
        )
    }

    private fun initErrorObservers() {
        viewModel.selectSubscribe(EntryDetailsViewState::errorSink)
            .observe(viewLifecycleOwner) { error ->
                if (error != null) {
                    errorMapper.map(error) { snackbar(it) }
                }
            }
        viewModel.selectSubscribe(EntryDetailsViewState::entry)
            .observe(viewLifecycleOwner) { item ->
                if (item is Fail) {
                    errorMapper.map(item.error) { snackbar(it) }
                }
            }
        viewModel.selectSubscribe(EntryDetailsViewState::activeDatabase)
            .observe(viewLifecycleOwner) { item ->
                if (item is Fail) {
                    errorMapper.map(item.error) { snackbar(it) }
                }
            }
    }

    override fun render(state: EntryDetailsViewState) {
        if (state.entry is Success) {
            state.entry()?.also {
                requireBinding().entryTitle.text = it.title
                requireBinding().entryIcon.setImageResource(iconMapper.map(it.iconId))

                ImageViewCompat.setImageTintList(
                    requireBinding().entryIcon,
                    ColorStateList.valueOf(
                        filterColorMapper.map(it.backgroundColor)
                    )
                )

                val tabs = if (state.historicEntryOf != null) {
                    listOf(
                        buildGeneralTab(it),
                        buildFilesTab(it, state)
                    )
                } else {
                    listOf(
                        buildGeneralTab(it),
                        buildFilesTab(it, state),
                        buildHistoryTab(it)
                    )
                }
                pagesAdapter.updateModels(tabs)

                if (requireBinding().pager.currentItem != currentPage) {
                    requireBinding().pager.setCurrentItem(currentPage, false)
                }
            }
        }
    }

    private fun buildGeneralTab(entry: KeyEntry): EpoxyController.() -> Unit = {
        buildProperty(R.string.details_username, entry.username)
        buildProperty(R.string.details_password, entry.password, true)
        buildProperty(R.string.details_website, entry.url)
        buildProperty(R.string.details_notes, entry.notes)

        try {
            OneTimePassword.from(entry)?.let {
                detailsOtp {
                    id(R.string.details_otp)
                    title(getString(R.string.details_otp))
                    isSurface(true)
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
                buildProperty(R.string.details_expires, expiryTime!!.time.formatAsDateTime())
            }
        }

        if (entry.tags.isNotEmpty()) {
            detailsTags {
                id("$ID_PROPERTY:${R.string.details_tags}")
                title(getString(R.string.details_tags))
                isSurface(true)
                tags(entry.tags)
            }
        }
    }

    private fun buildFilesTab(entry: KeyEntry, state: EntryDetailsViewState): EpoxyController.() -> Unit = {
        if (entry.attachments.size < 1) {
            info {
                id(ID_DOWNLOADS)
                title(getString(R.string.details_downloads_empty))
            }
        } else {
            state.activeDatabase()?.database?.meta?.binaries?.let { binaries ->
                for (item in entry.attachments) {
                    val fileSize = binaries.find { it.id == item.ref }?.data?.size?.toLong() ?: 0L
                    val isProcessing = state.saveAttachmentQueue.contains(item.ref)
                    val savedFileUri = state.savedAttachments[item.ref]

                    detailsAsset {
                        id(item.ref)
                        title(item.key)
                        subtitle(android.text.format.Formatter.formatShortFileSize(context, fileSize))
                        iconId(
                            if (savedFileUri != null) {
                                R.drawable.ic_arrow_simple_forward
                            } else {
                                R.drawable.ic_download
                            }
                        )
                        isClickable(!isProcessing)
                        isProcessing(isProcessing)
                        isSurface(true)
                        clickListener(View.OnClickListener { onAttachmentClicked(item) })
                        longClickListener { _ -> copyToClipboard(item.key, item.key) }
                    }
                }
            }
        }
    }

    private fun buildHistoryTab(entry: KeyEntry): EpoxyController.() -> Unit = {
        entry.times?.apply {
            creationTime?.let {
                buildProperty(R.string.details_date_created, it.time.formatAsDateTime(), isSurface = false)
            }

            lastModificationTime?.let {
                buildProperty(R.string.details_date_updated, it.time.formatAsDateTime(), isSurface = false)
            }
        }

        if (entry.history.isNotEmpty()) {
            detailsHeader {
                id(R.string.details_date_historic_title)
                title(getString(R.string.details_date_historic_title))
            }

            entry.history.forEachIndexed { i, item ->
                item.times?.lastModificationTime?.let {
                    detailsAsset {
                        id("$ID_HISTORIC_ENTRY:$i")
                        title(it.time.formatAsDateTime())
                        iconId(R.drawable.ic_clock)
                        isClickable(true)
                        clickListener(View.OnClickListener { onHistoricEntryClicked(item.uuid, entry.uuid) })
                    }
                }
            }
        }
    }

    private fun EpoxyController.buildProperty(
        @StringRes titleId: Int, content: String?,
        isMasked: Boolean = false, isSurface: Boolean = true
    ) = buildProperty("$ID_PROPERTY:$titleId", getString(titleId), content, isMasked, isSurface)

    private fun EpoxyController.buildProperty(
        id: String, title: String, content: String?,
        isMasked: Boolean = false, isSurface: Boolean = true
    ) {
        if (content != null && content.isNotBlank()) {
            if (isMasked) {
                detailsPropertyMasked {
                    id(id)
                    title(title)
                    subtitle(content)
                    isSurface(isSurface)
                    longClickListener { _ -> copyToClipboard(title, content) }
                }
            } else {
                detailsProperty {
                    id(id)
                    title(title)
                    subtitle(content)
                    isSurface(isSurface)
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

    private fun onAttachmentClicked(attachment: KeyAttachment) = viewModel.withState() { state ->
        val savedFileUri = state.savedAttachments[attachment.ref]

        if (savedFileUri != null) {
            val mimeType = attachment.key.getMimeTypeFromFileName()

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(Uri.parse(savedFileUri), mimeType)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(intent, getString(R.string.open_dialogs_open_with)))

        } else {
            saveAttachment(attachment)
        }
    }

    private fun saveAttachment(attachment: KeyAttachment) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
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

    private fun onHistoricEntryClicked(id: UUID, parentId: UUID) = viewModel.withState() { state ->
        findNavController().navigate(
            R.id.action_historic_entry_details,
            bundleOf(Args.KEY to EntryDetailsArgs(state.activeDatabaseId, id, parentId))
        )
    }

    companion object {
        private const val ID_PROPERTY = "PROPERTY"
        private const val ID_CUSTOM_PROPERTY = "CUSTOM_PROPERTY"
        private const val ID_DOWNLOADS = "DOWNLOADS"
        private const val ID_HISTORIC_ENTRY = "HISTORIC_ENTRY"

        private const val PAGES_FIRST = 0

        private val OTP_PROPERTIES = listOf("otp", "TOTP Seed", "TOTP Settings")
    }

}
