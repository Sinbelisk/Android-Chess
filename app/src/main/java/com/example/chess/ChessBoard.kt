package com.example.chess

import android.graphics.Color

class ChessBoard {
    private val boardSize = 8
    val boardMatrix = Array(boardSize) { Array<ChessPiece?>(boardSize) { null } }

    init {
        setupBoard()
    }

    // Contadores de piezas por equipo
    private var whitePieceCount = 16
    private var blackPieceCount = 16

    // Inicializa el tablero con las piezas en su posición inicial
    private fun setupBoard() {
        // Peones y piezas blancas
        boardMatrix[6] = Array(boardSize) { ChessPiece(1, Color.WHITE) } // Peones blancos
        boardMatrix[7] = arrayOf(
            ChessPiece(2, Color.WHITE), ChessPiece(3, Color.WHITE), ChessPiece(4, Color.WHITE), ChessPiece(6, Color.WHITE),
            ChessPiece(5, Color.WHITE), ChessPiece(4, Color.WHITE), ChessPiece(3, Color.WHITE), ChessPiece(2, Color.WHITE)
        )

        // Peones y piezas negras
        boardMatrix[0] = arrayOf(
            ChessPiece(-2, Color.BLACK), ChessPiece(-3, Color.BLACK), ChessPiece(-4, Color.BLACK), ChessPiece(-6, Color.BLACK),
            ChessPiece(-5, Color.BLACK), ChessPiece(-4, Color.BLACK), ChessPiece(-3, Color.BLACK), ChessPiece(-2, Color.BLACK)
        )
        boardMatrix[1] = Array(boardSize) { ChessPiece(-1, Color.BLACK) } // Peones negros
    }

    // Reinicia el tablero a su estado inicial
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

    // Obtiene la pieza en una posición específica
    fun getPieceAt(row: Int, col: Int): ChessPiece? {
        return if (isValidPosition(row, col)) boardMatrix[row][col] else null
    }

    // Mueve una pieza de una posición a otra
    fun movePiece(fromRow: Int, fromCol: Int, toRow: Int, toCol: Int): Boolean {
        if (!isValidPosition(fromRow, fromCol) || !isValidPosition(toRow, toCol)) return false

        val piece = boardMatrix[fromRow][fromCol]
        if (piece != null) {
            // Capturar pieza si existe en destino
            val targetPiece = boardMatrix[toRow][toCol]
            targetPiece?.let {
                if (it.isWhite()) {
                    whitePieceCount--
                } else {
                    blackPieceCount--
                }
            }

            // Mover pieza
            boardMatrix[toRow][toCol] = piece
            boardMatrix[fromRow][fromCol] = null

            return true
        }
        return false
    }

    // Obtiene la cantidad de piezas por equipo
    fun getPieceCount(isWhite: Boolean): Int = if (isWhite) whitePieceCount else blackPieceCount

    // Comprueba si un equipo ha perdido todas sus piezas
    fun hasLost(isWhite: Boolean): Boolean = getPieceCount(isWhite) == 0

    // Comprueba si una posición está vacía
    fun isEmpty(row: Int, col: Int): Boolean {
        return isValidPosition(row, col) && boardMatrix[row][col] == null
    }

    // Comprueba si una posición es válida en el tablero
    fun isValidPosition(row: Int, col: Int): Boolean {
        return row in 0 until boardSize && col in 0 until boardSize
    }
}
