package io.github.anvell.keemobile.common.permissions

import io.reactivex.Single
import io.reactivex.SingleEmitter
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionsProvider @Inject constructor() {

    private var proxy: PermissionsProxy? = null
    private val requestCounter = AtomicInteger(0)
    private val emitters: MutableMap<Int, SingleEmitter<List<Result>>> = mutableMapOf()

    fun registerProxy(proxy: PermissionsProxy) {
        this.proxy = proxy
    }

    fun unregisterProxy() {
        proxy = null
    }

    fun requestPermissions(vararg permissions: String) = Single.create<List<Result>> { emitter ->
        proxy?.apply {
            val code = requestCounter.getAndIncrement()
            emitters[code] = emitter
            requestPermissions(permissions, code)
        } ?: emitter.onError(RuntimeException("PermissionProxy is not bound to perform permission request."))
    }

    fun permissionsProcessed(requestCode: Int, permissions: List<String>, grantResults: List<Int>) {
        emitters[requestCode]?.let { emitter ->
            if (!emitter.isDisposed) {
                emitter.onSuccess(
                    permissions.mapIndexed { i, item ->
                        Result(item, grantResults[i])
                    }
                )
            }
            emitters.remove(requestCode)
        }
    }

    data class Result(val name: String, val status: Int)

}
