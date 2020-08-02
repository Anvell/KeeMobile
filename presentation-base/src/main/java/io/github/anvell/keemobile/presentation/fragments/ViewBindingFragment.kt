package io.github.anvell.keemobile.presentation.fragments

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class ViewBindingFragment<B>(
    @LayoutRes contentLayoutId: Int
) : BaseFragment(contentLayoutId) where B : ViewDataBinding {
    private var binding: B? = null

    protected fun requireBinding(): B = requireNotNull(binding)

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DataBindingUtil.bind(requireView())
    }

    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
