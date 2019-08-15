package io.github.anvell.keemobile.common.extensions

import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers

fun <T> Single<T>.scheduleOn(scheduler: Scheduler): Single<T> {
    return subscribeOn(scheduler)
        .observeOn(AndroidSchedulers.mainThread())
}
