package com.example.chess

import android.graphics.Color

/**
 * Representa el tablero de ajedrez, con un arreglo bidimensional que contiene las piezas en sus posiciones.
 * Esta clase gestiona la inicialización del tablero, el movimiento de las piezas, y verifica las condiciones de victoria.
 */
class ChessBoard {
    private val boardSize = 8
    val boardMatrix = Array(boardSize) { Array<ChessPiece?>(boardSize) { null } }

    // Contadores de piezas por equipo
    private var whitePieceCount = 16
    private var blackPieceCount = 16

    init {
        setupBoard()
    }

    /**
     * Inicializa el tablero con las piezas en su posición inicial.
     * Los peones y las piezas principales (torres, caballos, etc.) se colocan en las filas correspondientes.
     */
    private fun setupBoard() {
        // Peones blancos
        boardMatrix[6] = Array(boardSize) { ChessPiece(1, Color.WHITE) }

        // Piezas blancas
        boardMatrix[7] = arrayOf(
            ChessPiece(2, Color.WHITE), ChessPiece(3, Color.WHITE), ChessPiece(4, Color.WHITE), ChessPiece(6, Color.WHITE),
            ChessPiece(5, Color.WHITE), ChessPiece(4, Color.WHITE), ChessPiece(3, Color.WHITE), ChessPiece(2, Color.WHITE)
        )

        // Peones negros
        boardMatrix[1] = Array(boardSize) { ChessPiece(-1, Color.BLACK) }

        // Piezas negras
        boardMatrix[0] = arrayOf(
            ChessPiece(-2, Color.BLACK), ChessPiece(-3, Color.BLACK), ChessPiece(-4, Color.BLACK), ChessPiece(-6, Color.BLACK),
            ChessPiece(-5, Color.BLACK), ChessPiece(-4, Color.BLACK), ChessPiece(-3, Color.BLACK), ChessPiece(-2, Color.BLACK)
        )
    }

    /**
     * Reinicia el tablero a su estado inicial, vacía las celdas y restaura los contadores de piezas.
     */
    fun resetBoard() {
        for (row in 0 until boardSize) {
            for (col in 0 until boardSize) {
                boardMatrix[row][col] = null
            }
        }
        whitePieceCount = 16
        blackPieceCount = 16
        setupBoard()
    }

    /**
     * Obtiene la pieza en una posición específica del tablero.
     * @param row Fila de la posición.
     * @param col Columna de la posición.
     * @return La pieza en la posición especificada, o null si no hay ninguna.
     */
    fun getPieceAt(row: Int, col: Int): ChessPiece? {
        return if (isValidPosition(row, col)) boardMatrix[row][col] else null
    }

    /**
     * Mueve una pieza de una posición a otra. Si la celda de destino tiene una pieza del oponente, se captura.
     * @param fromRow Fila de la posición de origen.
     * @param fromCol Columna de la posición de origen.
     * @param toRow Fila de la posición de destino.
     * @param toCol Columna de la posición de destino.
     * @return true si el movimiento fue exitoso, false si fue inválido.
     */
    fun movePiece(fromRow: Int, fromCol: Int, toRow: Int, toCol: Int): Boolean {
        if (!isValidPosition(fromRow, fromCol) || !isValidPosition(toRow, toCol)) return false

        val piece = boardMatrix[fromRow][fromCol]
        if (piece != null) {
            // Captura la pieza del oponente si existe en la celda destino
            val targetPiece = boardMatrix[toRow][toCol]
            targetPiece?.let {
                if (it.isWhite()) {
                    whitePieceCount--
                } else {
                    blackPieceCount--
                }
            }

            // Mueve la pieza
            boardMatrix[toRow][toCol] = piece
            boardMatrix[fromRow][fromCol] = null

            return true
        }
        return false
    }

    /**
     * Obtiene la cantidad de piezas restantes para un equipo.
     * @param isWhite Indica si se consultan las piezas blancas (true) o negras (false).
     * @return El número de piezas restantes para el equipo especificado.
     */
    fun getPieceCount(isWhite: Boolean): Int = if (isWhite) whitePieceCount else blackPieceCount

    /**
     * Verifica si un equipo ha perdido todas sus piezas.
     * @param isWhite Indica si se está verificando el estado del equipo blanco (true) o negro (false).
     * @return true si el equipo ha perdido todas sus piezas, false en caso contrario.
     */
    fun hasLost(isWhite: Boolean): Boolean = getPieceCount(isWhite) == 0

    /**
     * Comprueba si una celda está vacía.
     * @param row Fila de la celda a comprobar.
     * @param col Columna de la celda a comprobar.
     * @return true si la celda está vacía, false si contiene una pieza.
     */
    fun isEmpty(row: Int, col: Int): Boolean {
        return isValidPosition(row, col) && boardMatrix[row][col] == null
    }

    /**
     * Verifica si una posición está dentro de los límites del tablero.
     * @param row Fila de la posición.
     * @param col Columna de la posición.
     * @return true si la posición es válida, false en caso contrario.
     */
    fun isValidPosition(row: Int, col: Int): Boolean {
        return row in 0 until boardSize && col in 0 until boardSize
    }
}
