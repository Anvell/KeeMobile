package io.github.anvell.keemobile.extensions

import android.app.Activity
import io.github.anvell.keemobile.di.InjectorProvider

val Activity.injector get() = (application as InjectorProvider).component
