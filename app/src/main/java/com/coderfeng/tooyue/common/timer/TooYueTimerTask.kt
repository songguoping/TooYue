package com.coderfeng.tooyue.common.timer

import java.util.*

class TooYueTimerTask(listener: OnTimerListener) : TimerTask() {
    private var delay: Long = 1000
    private var postTime: Long = 0
    private var mTimerState: TimerState = TimerState.IDLE
    private var listener: OnTimerListener = listener

    override fun run() {
        if (mTimerState == TimerState.START) {
            this.postTime += delay
            this.listener.onTimerUpdate(postTime, delay)
        }
    }


    fun setDelay(delay: Long) {
        this.delay = delay
    }

    fun start() {
        this.mTimerState = TimerState.START
    }

    fun resume() {
        this.mTimerState = TimerState.START
    }

    fun pause() {
        this.mTimerState = TimerState.PAUSE
    }

    fun stop() {
        this.mTimerState = TimerState.IDLE
        this.postTime = 0
        cancel()
    }

    fun updatePostTime(newPostTime: Long) {
        this.postTime = newPostTime
    }

    fun getTimerState(): TimerState {
        return mTimerState
    }

    fun getDelay(): Long {
        return delay
    }
}

interface OnTimerListener {
    fun onTimerUpdate(time: Long, delay: Long)
}

enum class TimerState {
    IDLE, START, PAUSE
}