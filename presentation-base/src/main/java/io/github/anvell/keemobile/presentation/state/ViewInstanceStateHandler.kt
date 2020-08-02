package io.github.anvell.keemobile.presentation.state

import android.os.Bundle
import android.os.Parcelable

interface ViewInstanceStateHandler : InstanceStateStore {

    fun saveState(superState: Parcelable?): Parcelable {
        return Bundle().apply {
            putParcelable(SUPER_STATE_TAG, superState)
            putBundle(BUNDLE_TAG, stateBundle)
        }
    }

    fun restoreState(state: Parcelable?): Parcelable? {
        return if (state is Bundle) {
            stateBundle.putAll(state.getBundle(BUNDLE_TAG))
            state.getParcelable(SUPER_STATE_TAG)
        } else {
            state
        }
    }

    companion object {
        private const val BUNDLE_TAG = "STATE_SAVER"
        private const val SUPER_STATE_TAG = "SUPER_STATE"
    }
}
