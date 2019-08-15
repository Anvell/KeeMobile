package io.github.anvell.keemobile.common.rx

import io.reactivex.Scheduler

interface RxSchedulers {

    fun io(): Scheduler

    fun main(): Scheduler

    fun computation(): Scheduler
}
