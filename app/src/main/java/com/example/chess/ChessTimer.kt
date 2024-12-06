package com.example.chess

import android.os.CountDownTimer

class ChessTimer(private val timeLimit: Long, private val onTimeUpdate: (Long) -> Unit, private val onTimeUp: () -> Unit) {
    private var remainingTime: Long = timeLimit
    private var countDownTimer: CountDownTimer? = null

    fun start() {
        countDownTimer?.cancel() // Cancelar cualquier temporizador anterior
        countDownTimer = object : CountDownTimer(remainingTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTime = millisUntilFinished
                onTimeUpdate(remainingTime)
            }

            override fun onFinish() {
                onTimeUp()
            }
        }.start()
    }

    fun reset() {
        remainingTime = timeLimit
        onTimeUpdate(remainingTime)
    }

    fun cancel() {
        countDownTimer?.cancel()
    }

    fun getFormattedTime(): String {
        val minutes = (remainingTime / 1000) / 60
        val seconds = (remainingTime / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}
