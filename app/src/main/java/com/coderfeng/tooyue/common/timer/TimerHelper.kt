package com.coderfeng.tooyue.common.timer

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class TimerHelper(listener: OnTimerListener) {

    private var service: ScheduledExecutorService? = null
    private var mTimer: ScheduledFuture<*>? = null
    private var mHealthTimer: TooYueTimerTask? = null
    private var mTimerListener: OnTimerListener? = null

    init {
        mTimerListener = listener
        service = Executors.newScheduledThreadPool(1)
        mHealthTimer = mTimerListener?.let { TooYueTimerTask(it) }
    }

    fun startTimer() {
        if (mHealthTimer?.getTimerState() !== TimerState.START) {
            mHealthTimer?.start()
            mTimer = service!!.scheduleAtFixedRate(
                mHealthTimer,
                0,
                946,
                TimeUnit.MILLISECONDS
            )
        }
    }

    fun pauseTimer() {
        mHealthTimer?.pause()
    }

    fun resumeTimer() {
        mHealthTimer?.resume()
    }

    fun stopTimer() {
        mHealthTimer?.stop()
        mHealthTimer = null
        mTimer?.cancel(true)
        mTimer = null
        service = null
    }

    fun updatePostTime(newPostTime: Long) {
        mHealthTimer?.updatePostTime(newPostTime)
    }

    fun setDelay(delay: Long) {
        mHealthTimer?.setDelay(delay)
    }

}