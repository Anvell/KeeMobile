package io.github.anvell.keemobile.core.ui.modals

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.anvell.keemobile.core.constants.Args
import io.github.anvell.keemobile.core.ui.R
import io.github.anvell.keemobile.core.ui.databinding.FragmentBottomSheetBinding
import io.github.anvell.keemobile.core.ui.dialogHeader
import io.github.anvell.keemobile.core.ui.dialogRow
import io.github.anvell.keemobile.core.ui.extensions.clipToCornerRadius
import io.github.anvell.keemobile.core.ui.extensions.show
import io.github.anvell.keemobile.core.ui.widgets.DividerDecoration

class ModalBottomSheet : ViewDataBindingBottomSheet<FragmentBottomSheetBinding>(
    FragmentBottomSheetBinding::inflate
) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireBinding().recycler.clipToCornerRadius(resources.getDimension(R.dimen.surface_corner_radius))
        requireBinding().recycler.addItemDecoration(
            DividerDecoration(
                requireContext(),
                R.drawable.list_divider,
                LinearLayoutManager.VERTICAL
            )
        )
        render(
            arguments?.get(Args.KEY) as? ModalBottomSheetContent
                ?: error("Invalid arguments supplied.")
        )
    }

    fun show(manager: FragmentManager, content: ModalBottomSheetContent) {
        show(manager, bundleOf(Args.KEY to content))
    }

    private fun render(content: ModalBottomSheetContent) {
        requireBinding().recycler.withModels {
            dialogHeader {
                id(content.headerId)
                title(getString(content.headerId))
            }
            content.rows.forEach { row ->
                dialogRow {
                    id(row.titleId)
                    title(getString(row.titleId))
                    isSelected(false)
                    isClickable(true)
                    clickListener(View.OnClickListener {
                        setFragmentResult(content.destinationKey, bundleOf(Args.KEY to row.content))
                        dismiss()
                    })
                }
            }
        }
    }
}
