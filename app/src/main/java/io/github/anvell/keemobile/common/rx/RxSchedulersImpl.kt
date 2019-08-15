package io.github.anvell.keemobile.common.rx

import dagger.Reusable
import io.github.anvell.keemobile.common.rx.RxSchedulers
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@Reusable
class RxSchedulersImpl @Inject constructor() : RxSchedulers {

    override fun io() = Schedulers.io()

    override fun main() = AndroidSchedulers.mainThread()

    override fun computation() = Schedulers.computation()
}
