package com.example.chess

import android.graphics.Color

data class ChessPiece(
    val type: Int, // El tipo de la pieza: 1, 2, 3, etc.
    val color: Int // Color de la pieza: Color.WHITE o Color.BLACK
) {
    fun getUnicode(): String {
        return when (type) {
            1, -1 -> "♟" // Peón
            2, -2 -> "♜" // Torre
            3, -3 -> "♞" // Caballo
            4, -4 -> "♝" // Alfil
            5, -5 -> "♛" // Reina
            6, -6 -> "♚" // Rey
            else -> ""
        }
    }

    fun isWhite() = color == Color.WHITE
    fun isBlack() = color == Color.BLACK
}
