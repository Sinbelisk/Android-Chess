package com.example.chess

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import kotlin.math.abs

/**
 * Controlador de la lógica del juego de ajedrez.
 * Maneja el estado de las piezas, los turnos y los movimientos.
 *
 * @property board El tablero de ajedrez.
 * @property context El contexto de la aplicación.
 */
class ChessController(private val board: ChessBoard, context: Context) {

    private var selectedRow: Int? = null
    private var selectedCol: Int? = null
    private var isWhiteTurn = true // El jugador blanco comienza

    var onPieceMoved: ((fromRow: Int, fromCol: Int, toRow: Int, toCol: Int) -> Unit)? = null
    var onTurnChanged: ((isWhiteTurn: Boolean) -> Unit)? = null

    private val soundPool: SoundPool
    private val soundMap: MutableMap<Int, Int> = mutableMapOf()

    init {
        // Inicializa SoundPool y carga los sonidos
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(attributes)
            .build()

        loadSounds(context)
    }

    /**
     * Carga los sonidos para cada tipo de pieza.
     */
    private fun loadSounds(context: Context) {
        soundMap[1] = soundPool.load(context, R.raw.pawn_move, 1)   // Peón
        soundMap[2] = soundPool.load(context, R.raw.rook_move, 1)   // Torre
        soundMap[3] = soundPool.load(context, R.raw.knight_move, 1) // Caballo
        soundMap[4] = soundPool.load(context, R.raw.bishop_move, 1) // Alfil
        soundMap[5] = soundPool.load(context, R.raw.queen_move, 1)  // Reina
        soundMap[6] = soundPool.load(context, R.raw.king_move, 1)   // Rey
    }

    /**
     * Maneja el toque en una celda del tablero.
     */
    fun handleCellTouch(row: Int, col: Int) {
        val piece = board.getPieceAt(row, col)

        if (selectedRow != null && selectedCol != null) {
            val fromRow = selectedRow!!
            val fromCol = selectedCol!!
            val selectedPiece = board.getPieceAt(fromRow, fromCol)

            if (selectedPiece != null && isValidMove(selectedPiece, row, col)) {
                if (board.movePiece(fromRow, fromCol, row, col)) {
                    onPieceMoved?.invoke(fromRow, fromCol, row, col)
                    playMoveSound(selectedPiece) // Reproducir el sonido del movimiento
                    changeTurn()
                }
            }
            selectedRow = null
            selectedCol = null
        } else if (piece != null && isCurrentPlayerPiece(piece)) {
            selectedRow = row
            selectedCol = col
        }
    }

    /**
     * Reproduce el sonido correspondiente al tipo de pieza.
     */
    private fun playMoveSound(piece: ChessPiece) {
        val soundId = soundMap[abs(piece.type)]
        soundId?.let {
            soundPool.play(it, 1f, 1f, 0, 0, 1f)
        }
    }

    /**
     * Verifica si la pieza seleccionada pertenece al jugador actual.
     */
    private fun isCurrentPlayerPiece(piece: ChessPiece): Boolean {
        return (isWhiteTurn && piece.isWhite()) || (!isWhiteTurn && piece.isBlack())
    }

    /**
     * Valida si el movimiento es permitido.
     */
    private fun isValidMove(piece: ChessPiece, toRow: Int, toCol: Int): Boolean {
        val targetPiece = board.getPieceAt(toRow, toCol)
        return targetPiece == null || targetPiece.color != piece.color
    }

    /**
     * Cambia el turno entre los jugadores.
     */
    private fun changeTurn() {
        isWhiteTurn = !isWhiteTurn
        onTurnChanged?.invoke(isWhiteTurn)
    }

    /**
     * Obtiene las coordenadas de la celda seleccionada.
     */
    fun getSelectedCell(): Pair<Int?, Int?> = Pair(selectedRow, selectedCol)

    /**
     * Reinicia la selección de pieza.
     */
    fun clearSelection() {
        selectedRow = null
        selectedCol = null
    }
}



