package io.github.anvell.keemobile.core.ui.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import io.github.anvell.keemobile.core.constants.RequestCodes
import io.github.anvell.keemobile.core.extensions.persistReadWritePermissions
import io.github.anvell.keemobile.core.io.ClipboardProvider
import io.github.anvell.keemobile.core.rx.RxSchedulers
import io.github.anvell.keemobile.core.ui.ativities.DrawerHolder
import io.github.anvell.keemobile.core.ui.mappers.ErrorMapper
import io.github.anvell.keemobile.core.ui.state.InstanceStateHandler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import javax.inject.Inject

abstract class BaseFragment : Fragment(), InstanceStateHandler {
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
}
