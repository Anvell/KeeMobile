package io.github.anvell.keemobile.presentation.base

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.ViewDataBinding
import com.airbnb.mvrx.Async
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.MvRxState
import com.google.android.material.snackbar.Snackbar
import io.github.anvell.keemobile.common.constants.RequestCodes
import io.github.anvell.keemobile.common.extensions.persistReadWritePermissions
import io.github.anvell.keemobile.common.io.ClipboardProvider
import io.github.anvell.keemobile.common.mapper.ErrorMapper
import io.github.anvell.keemobile.common.rx.RxSchedulers
import io.github.anvell.keemobile.presentation.home.DrawerHolder
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import javax.inject.Inject
import kotlin.reflect.KProperty1

abstract class BaseFragment<T>(
    val inflaterBlock: (
        inflater: LayoutInflater,
        root: ViewGroup?, attachToRoot: Boolean
    ) -> T
) : BaseMvRxFragment() where T : ViewDataBinding {

    @Inject
    lateinit var rxSchedulers: RxSchedulers

    @Inject
    lateinit var errorMapper: ErrorMapper

    @Inject
    lateinit var clipboardProvider: ClipboardProvider

    protected lateinit var binding: T

    private val disposables = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflaterBlock(inflater, container, false)
        return binding.root
    }

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

    override fun onStop() {
        disposables.clear()
        super.onStop()
    }

    protected fun Disposable.disposeOnStop(): Disposable {
        disposables.add(this)
        return this
    }

    protected fun hideSoftKeyboard() {
        val manager = requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
        manager?.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    protected fun <S : MvRxState, V> snackbarOnFailedState(
        viewModel: BaseViewModel<S>,
        vararg asyncProps: KProperty1<S, Async<V>>
    ) {
        for (property in asyncProps) {
            viewModel.asyncSubscribe(property, onFail = { t ->
                val message = errorMapper.map(t)
                if (message != null && view != null) {
                    Snackbar.make(view!!, message, Snackbar.LENGTH_SHORT).show()
                }
            }, deliveryMode = uniqueOnly())
        }
    }

    protected fun requestCreateFile(name: String) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_TITLE, name)
            flags = flags and (Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            type = "*/*"
        }
        startActivityForResult(intent, RequestCodes.FILE_CREATE)
    }

    protected fun requestOpenFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            flags = flags and (Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            type = "*/*"
        }
        startActivityForResult(intent, RequestCodes.FILE_OPEN)
    }

    protected open fun onFileCreated(uri: Uri) = Unit

    protected open fun onFileOpened(uri: Uri) = Unit

    protected open fun getDrawer() = (activity as? DrawerHolder)?.getDrawer()

}
