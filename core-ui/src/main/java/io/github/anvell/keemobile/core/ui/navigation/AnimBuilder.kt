package io.github.anvell.keemobile.core.ui.navigation

import androidx.navigation.AnimBuilder
import io.github.anvell.keemobile.core.ui.R

fun AnimBuilder.slideFromSide() {
    enter = R.anim.slide_in_from_right
    exit = R.anim.slide_out_to_left
    popEnter = R.anim.slide_in_from_left
    popExit = R.anim.slide_out_to_right
}

fun AnimBuilder.pushUp() {
    enter = R.anim.push_up_in
    exit = R.anim.push_up_out
    popEnter = R.anim.push_up_pop_in
    popExit = R.anim.push_up_pop_out
}

fun AnimBuilder.fadeIn() {
    enter = R.anim.fade_enter
    exit = R.anim.fade_exit
    popEnter = R.anim.fade_enter
    popExit = R.anim.fade_exit
}
