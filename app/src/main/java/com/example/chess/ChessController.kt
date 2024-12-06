package com.example.chess

class ChessController(private val board: ChessBoard) {

    // Estado de interacción
    private var selectedRow: Int? = null
    private var selectedCol: Int? = null

    // Listener para eventos de interacción
    var onPieceMoved: ((fromRow: Int, fromCol: Int, toRow: Int, toCol: Int) -> Unit)? = null

    // Maneja los eventos táctiles en el tablero
    fun handleCellTouch(row: Int, col: Int) {
        val piece = board.getPieceAt(row, col)

        if (selectedRow != null && selectedCol != null) {
            // Mover la pieza seleccionada
            val fromRow = selectedRow!!
            val fromCol = selectedCol!!
            if (board.movePiece(fromRow, fromCol, row, col)) {
                onPieceMoved?.invoke(fromRow, fromCol, row, col)
            }
            selectedRow = null
            selectedCol = null
        } else if (piece != null) {
            // Seleccionar una pieza
            selectedRow = row
            selectedCol = col
        }
    }

    // Obtiene las coordenadas seleccionadas
    fun getSelectedCell(): Pair<Int?, Int?> = Pair(selectedRow, selectedCol)

    // Reinicia la selección
    fun clearSelection() {
        selectedRow = null
        selectedCol = null
    }
}
