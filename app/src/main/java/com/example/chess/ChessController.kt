package com.example.chess

class ChessController(private val board: ChessBoard) {

    // Estado de interacción
    private var selectedRow: Int? = null
    private var selectedCol: Int? = null

    // Control de turnos
    private var isWhiteTurn = true // Blanco comienza

    // Listener para eventos de interacción
    var onPieceMoved: ((fromRow: Int, fromCol: Int, toRow: Int, toCol: Int) -> Unit)? = null
    var onTurnChanged: ((isWhiteTurn: Boolean) -> Unit)? = null

    // Maneja los eventos táctiles en el tablero
    fun handleCellTouch(row: Int, col: Int) {
        val piece = board.getPieceAt(row, col)

        if (selectedRow != null && selectedCol != null) {
            // Intentar mover la pieza seleccionada
            val fromRow = selectedRow!!
            val fromCol = selectedCol!!
            val selectedPiece = board.getPieceAt(fromRow, fromCol)

            if (selectedPiece != null && isValidMove(selectedPiece, row, col)) {
                if (board.movePiece(fromRow, fromCol, row, col)) {
                    onPieceMoved?.invoke(fromRow, fromCol, row, col)
                    changeTurn() // Cambiar el turno después de un movimiento exitoso
                }
            }
            selectedRow = null
            selectedCol = null
        } else if (piece != null && isCurrentPlayerPiece(piece)) {
            // Seleccionar una pieza si es del jugador actual
            selectedRow = row
            selectedCol = col
        }
    }

    // Comprueba si una pieza pertenece al jugador actual
    private fun isCurrentPlayerPiece(piece: ChessPiece): Boolean {
        return (isWhiteTurn && piece.isWhite()) || (!isWhiteTurn && piece.isBlack())
    }

    // Valida el movimiento de acuerdo con las reglas básicas
    private fun isValidMove(piece: ChessPiece, toRow: Int, toCol: Int): Boolean {
        val targetPiece = board.getPieceAt(toRow, toCol)
        // No permitir capturar piezas propias
        if (targetPiece != null && targetPiece.color == piece.color) return false
        // Aquí puedes agregar más validaciones específicas para cada tipo de pieza
        return true
    }

    // Cambiar el turno
    private fun changeTurn() {
        isWhiteTurn = !isWhiteTurn
        onTurnChanged?.invoke(isWhiteTurn)
    }

    // Obtiene las coordenadas seleccionadas
    fun getSelectedCell(): Pair<Int?, Int?> = Pair(selectedRow, selectedCol)

    // Reinicia la selección
    fun clearSelection() {
        selectedRow = null
        selectedCol = null
    }
}

