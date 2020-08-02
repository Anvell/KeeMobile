package io.github.anvell.keemobile.core.rx

import io.reactivex.Scheduler

interface RxSchedulers {

    fun io(): Scheduler

    fun main(): Scheduler

    fun computation(): Scheduler
}
