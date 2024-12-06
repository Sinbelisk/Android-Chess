package com.example.chess

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import kotlin.math.abs

class ChessController(private val board: ChessBoard, context: Context) {

    // Estado de interacción
    private var selectedRow: Int? = null
    private var selectedCol: Int? = null

    // Control de turnos
    private var isWhiteTurn = true // Blanco comienza

    // Listener para eventos de interacción
    var onPieceMoved: ((fromRow: Int, fromCol: Int, toRow: Int, toCol: Int) -> Unit)? = null
    var onTurnChanged: ((isWhiteTurn: Boolean) -> Unit)? = null

    // SoundPool para manejar los sonidos
    private val soundPool: SoundPool
    private val soundMap: MutableMap<Int, Int> = mutableMapOf()

    init {
        // Inicializar SoundPool con atributos
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(attributes)
            .build()

        // Cargar los sonidos de cada tipo de pieza
        loadSounds(context)
    }

    private fun loadSounds(context: Context) {
        soundMap[1] = soundPool.load(context, R.raw.pawn_move, 1) // Sonido de peón
        soundMap[2] = soundPool.load(context, R.raw.rook_move, 1) // Sonido de torre
        soundMap[3] = soundPool.load(context, R.raw.knight_move, 1) // Sonido de caballo
        soundMap[4] = soundPool.load(context, R.raw.bishop_move, 1) // Sonido de alfil
        soundMap[5] = soundPool.load(context, R.raw.queen_move, 1) // Sonido de reina
        soundMap[6] = soundPool.load(context, R.raw.king_move, 1) // Sonido de rey
    }

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
                    playMoveSound(selectedPiece) // Reproducir el sonido correspondiente a la pieza
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

    private fun playMoveSound(piece: ChessPiece) {
        // Usar el valor absoluto de 'type' para encontrar el sonido adecuado
        val soundId = soundMap[abs(piece.type)]
        soundId?.let {
            soundPool.play(it, 1f, 1f, 0, 0, 1f)
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

