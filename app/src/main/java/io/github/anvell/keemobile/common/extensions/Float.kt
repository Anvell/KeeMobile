package io.github.anvell.keemobile.common.extensions

import android.content.res.Resources

fun Float.toDp() = this / Resources.getSystem().displayMetrics.density

fun Float.toPx() = this * Resources.getSystem().displayMetrics.density

fun Float.toPxScaled() = this * Resources.getSystem().displayMetrics.scaledDensity
