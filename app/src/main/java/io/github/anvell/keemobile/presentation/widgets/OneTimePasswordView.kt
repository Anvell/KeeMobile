package io.github.anvell.keemobile.presentation.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import dagger.hilt.android.internal.managers.ViewComponentManager
import io.github.anvell.keemobile.R
import io.github.anvell.keemobile.common.authentication.OneTimePassword
import kotlinx.coroutines.*
import org.threeten.bp.Instant
import kotlin.math.ceil

class OneTimePasswordView(context: Context, attrs: AttributeSet?) :
    AppCompatTextView(context, attrs), LifecycleObserver {

    private var otpJob: Job? = null
    private val viewScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private var progressBarId: Int = -1
    private var progressBar: ProgressBar? = null
    private var oneTimePassword: OneTimePassword? = null

    init {
        val attributes = context.theme.obtainStyledAttributes(attrs, R.styleable.OneTimePasswordView, 0, 0)

        try {
            progressBarId = attributes.getResourceId(R.styleable.OneTimePasswordView_progressBar, -1)
        } finally {
            attributes.recycle()
        }

        when (context) {
            is LifecycleOwner -> context
            is ViewComponentManager.FragmentContextWrapper -> context.fragment
            else -> error("Context does not implement LifecycleOwner.")
        }.lifecycle.addObserver(this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        if (progressBarId > 0) {
            progressBar = (parent as View).findViewById(progressBarId)
        }
        resume()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        pause()
    }

    fun setOneTimePassword(otp: OneTimePassword) {
        oneTimePassword = otp
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    internal fun resume() {
        oneTimePassword?.let {
            otpJob?.cancel()
            otpJob = launchOtp(it)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    internal fun pause() {
        otpJob?.cancel()
    }

    private fun launchOtp(otp: OneTimePassword) = viewScope.launch(Dispatchers.Default) {
        var currentStep = 0.0

        do {
            val step = Instant.now().epochSecond.toDouble() / otp.period
            val rounded = ceil(step)
            progressBar?.progress = ((rounded - step) * 100).toInt()

            if (rounded > currentStep) {
                currentStep = rounded
                val result = otp.calculate()

                launch(Dispatchers.Main) {
                    text = result
                }
            }
            delay(1000)
        } while (true)
    }
}
