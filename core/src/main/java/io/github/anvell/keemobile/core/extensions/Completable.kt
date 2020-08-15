package io.github.anvell.keemobile.core.extensions

import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers

fun Completable.scheduleOn(scheduler: Scheduler): Completable {
    return subscribeOn(scheduler)
        .observeOn(AndroidSchedulers.mainThread())
}
