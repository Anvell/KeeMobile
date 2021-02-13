package io.github.anvell.keemobile.core.ui.modals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.databinding.ViewDataBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class ViewDataBindingBottomSheet<B>(
    val inflaterBlock: (
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToRoot: Boolean
    ) -> B
) : BottomSheetDialogFragment() where B : ViewDataBinding {
    private var binding: B? = null

    protected fun requireBinding(): B = requireNotNull(binding)

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflaterBlock(inflater, container, false)
        .also { binding = it }.root

    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
