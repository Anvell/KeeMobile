package io.github.anvell.keemobile.common.extensions

import java.text.DateFormat
import java.util.*

fun Date.formatAsDateTime(
    timeStyle: Int = DateFormat.SHORT,
    zone: TimeZone = TimeZone.getDefault()
): String {
    val formatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, timeStyle).apply {
        timeZone = zone
    }

    return formatter.format(this)
}

fun Date.formatAsDate(
    dateStyle: Int = DateFormat.MEDIUM,
    zone: TimeZone = TimeZone.getDefault()
): String {
    val formatter = DateFormat.getDateInstance(dateStyle).apply {
        timeZone = zone
    }

    return formatter.format(this)
}
