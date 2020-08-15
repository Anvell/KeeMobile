package io.github.anvell.keemobile.core.ui.extensions

import kotlin.math.max

fun <T> List<T>.append(element: T, trimTo: Int = 0): List<T> {
    require(trimTo >= 0) { "Requested element count $trimTo is less than zero." }

    return if (trimTo > 0) {
        this.drop(max((size + 1) - trimTo, 0)) + element
    } else {
        this + element
    }
}
