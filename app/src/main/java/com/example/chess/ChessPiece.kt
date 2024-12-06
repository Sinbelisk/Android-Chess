package com.example.chess

import android.graphics.Color

/**
 * Representa una pieza de ajedrez.
 *
 * @property type El tipo de la pieza: un valor entero que indica el tipo de la pieza.
 *                 Los valores son positivos para piezas blancas y negativos para piezas negras.
 * @property color El color de la pieza: `Color.WHITE` para piezas blancas y `Color.BLACK` para piezas negras.
 */
data class ChessPiece(
    val type: Int, // El tipo de la pieza: 1, 2, 3, etc.
    val color: Int // Color de la pieza: Color.WHITE o Color.BLACK
) {
    /**
     * Devuelve el símbolo Unicode que representa la pieza en el tablero de ajedrez.
     *
     * @return El símbolo Unicode correspondiente al tipo de la pieza.
     */
    fun getUnicode(): String {
        return when (type) {
            1, -1 -> "♟" // Peón
            2, -2 -> "♜" // Torre
            3, -3 -> "♞" // Caballo
            4, -4 -> "♝" // Alfil
            5, -5 -> "♛" // Reina
            6, -6 -> "♚" // Rey
            else -> "" // Si no es un tipo de pieza reconocido, se devuelve una cadena vacía.
        }
    }

    /**
     * Verifica si la pieza es blanca.
     *
     * @return `true` si la pieza es blanca (su color es `Color.WHITE`), `false` si no lo es.
     */
    fun isWhite() = color == Color.WHITE

    /**
     * Verifica si la pieza es negra.
     *
     * @return `true` si la pieza es negra (su color es `Color.BLACK`), `false` si no lo es.
     */
    fun isBlack() = color == Color.BLACK
}

