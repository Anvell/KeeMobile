package io.github.anvell.keemobile.core.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

object Spacers {
    @Composable
    fun Xxs() {
        Spacer(Modifier.size(2.dp))
    }

    @Composable
    fun Xs() {
        Spacer(Modifier.size(6.dp))
    }

    @Composable
    fun S() {
        Spacer(Modifier.size(10.dp))
    }

    @Composable
    fun M() {
        Spacer(Modifier.size(15.dp))
    }

    @Composable
    fun L() {
        Spacer(Modifier.size(20.dp))
    }

    @Composable
    fun Xl() {
        Spacer(Modifier.size(32.dp))
    }

    @Composable
    fun Xxl() {
        Spacer(Modifier.size(42.dp))
    }
}
