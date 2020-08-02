package io.github.anvell.keemobile.core.extensions

import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers

fun <T> Flowable<T>.scheduleOn(scheduler: Scheduler): Flowable<T> {
    return subscribeOn(scheduler)
        .observeOn(AndroidSchedulers.mainThread())
}
