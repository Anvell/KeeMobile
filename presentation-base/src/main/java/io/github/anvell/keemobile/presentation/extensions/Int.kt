package io.github.anvell.keemobile.presentation.extensions

import android.content.res.Resources

fun Int.toDp() = (this / Resources.getSystem().displayMetrics.density).toInt()

fun Int.toPx() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Int.toPxScaled() = (this * Resources.getSystem().displayMetrics.scaledDensity).toInt()
