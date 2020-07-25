package io.github.anvell.keemobile.common.state

import android.os.Bundle

interface InstanceStateHandler : InstanceStateStore {

    fun saveState(outState: Bundle) {
        outState.putBundle(BUNDLE_TAG, stateBundle)
    }

    fun restoreState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            stateBundle.putAll(savedInstanceState.getBundle(BUNDLE_TAG))
        }
    }

    companion object {
        private const val BUNDLE_TAG = "STATE_SAVER"
    }
}
