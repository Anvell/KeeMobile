package io.github.anvell.keemobile.common.extensions

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers

fun <T> Observable<T>.scheduleOn(scheduler: Scheduler): Observable<T> {
    return subscribeOn(scheduler)
        .observeOn(AndroidSchedulers.mainThread())
}
