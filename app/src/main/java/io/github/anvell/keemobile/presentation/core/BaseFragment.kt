package io.github.anvell.keemobile.presentation.core

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.airbnb.mvrx.BaseMvRxFragment
import io.github.anvell.keemobile.common.constants.RequestCodes

abstract class BaseFragment<T>(
    val inflaterBlock: (
        inflater: LayoutInflater,
        root: ViewGroup?, attachToRoot: Boolean
    ) -> T
) : BaseMvRxFragment() where T : ViewDataBinding {

    protected lateinit var binding: T

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = inflaterBlock(inflater, container, false)
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            RequestCodes.FILE_OPEN -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.data?.also {
                        onFileOpened(it)
                    }
                }
            }
        }
    }

    protected fun requestOpenFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            flags = flags and (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            type = "*/*"
        }
        startActivityForResult(intent, RequestCodes.FILE_OPEN)
    }

    protected open fun onFileOpened(uri: Uri) = Unit
}
