package com.example.chess

import android.os.CountDownTimer

/**
 * Temporizador para controlar el tiempo de los jugadores en una partida de ajedrez.
 *
 * @property timeLimit El tiempo límite en milisegundos para el temporizador.
 * @property onTimeUpdate Función de callback que se llama con el tiempo restante actualizado cada segundo.
 * @property onTimeUp Función de callback que se llama cuando el tiempo se acaba.
 */
class ChessTimer(
    private val timeLimit: Long, // Tiempo límite en milisegundos
    private val onTimeUpdate: (Long) -> Unit, // Callback para actualizar el tiempo
    private val onTimeUp: () -> Unit // Callback cuando se acaba el tiempo
) {
    private var remainingTime: Long = timeLimit // Tiempo restante
    private var countDownTimer: CountDownTimer? = null // Temporizador de cuenta atrás

    /**
     * Inicia el temporizador.
     */
    fun start() {
        countDownTimer?.cancel() // Cancela cualquier temporizador anterior
        countDownTimer = object : CountDownTimer(remainingTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTime = millisUntilFinished // Actualiza el tiempo restante
                onTimeUpdate(remainingTime) // Llama al callback con el tiempo actualizado
            }

            override fun onFinish() {
                onTimeUp() // Llama al callback cuando el tiempo se acaba
            }
        }.start() // Inicia el temporizador
    }

    /**
     * Cancela el temporizador si está en ejecución.
     */
    fun cancel() {
        countDownTimer?.cancel()
    }

    /**
     * Devuelve el tiempo restante formateado en el formato "mm:ss".
     */
    fun getFormattedTime(): String {
        val minutes = (remainingTime / 1000) / 60
        val seconds = (remainingTime / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}


