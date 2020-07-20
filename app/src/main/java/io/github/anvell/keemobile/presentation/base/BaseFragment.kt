package io.github.anvell.keemobile.presentation.base

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import io.github.anvell.keemobile.common.constants.RequestCodes
import io.github.anvell.keemobile.common.extensions.persistReadWritePermissions
import io.github.anvell.keemobile.common.io.ClipboardProvider
import io.github.anvell.keemobile.common.mapper.ErrorMapper
import io.github.anvell.keemobile.common.rx.RxSchedulers
import io.github.anvell.keemobile.common.state.NullableStateProperty
import io.github.anvell.keemobile.common.state.StateHandler
import io.github.anvell.keemobile.common.state.StateProperty
import io.github.anvell.keemobile.presentation.home.DrawerHolder
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import javax.inject.Inject

abstract class BaseFragment(@LayoutRes contentLayoutId: Int) : Fragment(contentLayoutId), StateHandler {
    @Inject
    lateinit var rxSchedulers: RxSchedulers

    @Inject
    lateinit var errorMapper: ErrorMapper

    @Inject
    lateinit var clipboardProvider: ClipboardProvider

    private val disposables = CompositeDisposable()

    override val stateBundle = Bundle()

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        restoreState(savedInstanceState)
        super.onCreate(savedInstanceState)
    }

    @CallSuper
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                RequestCodes.FILE_CREATE -> data?.data?.also { uri ->
                    uri.persistReadWritePermissions(requireContext())
                    onFileCreated(uri)
                }

                RequestCodes.FILE_OPEN -> data?.data?.also { uri ->
                    uri.persistReadWritePermissions(requireContext())
                    onFileOpened(uri)
                }
            }
        }
    }

    @CallSuper
    override fun onStop() {
        disposables.clear()
        super.onStop()
    }

    @CallSuper
    override fun onSaveInstanceState(outState: Bundle) {
        saveState(outState)
        super.onSaveInstanceState(outState)
    }

    protected fun Disposable.disposeOnStop(): Disposable {
        disposables.add(this)
        return this
    }

    protected open fun onFileCreated(uri: Uri) = Unit

    protected open fun onFileOpened(uri: Uri) = Unit

    protected open fun getDrawer() = (activity as? DrawerHolder)?.getDrawer()

    protected fun <T> stateProperty() = NullableStateProperty<T>()

    protected fun <T> stateProperty(defaultValue: T) = StateProperty(defaultValue)

}
