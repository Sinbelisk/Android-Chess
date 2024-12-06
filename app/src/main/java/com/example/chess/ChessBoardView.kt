package com.example.chess

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AlertDialog
/**
 * Vista personalizada para el tablero de ajedrez.
 * Esta clase es responsable de dibujar el tablero, las piezas, los temporizadores y manejar la interacción táctil.
 * También gestiona los cambios de turno, el control del tiempo de cada jugador y la detección de victoria o derrota.
 *
 * @param context El contexto de la aplicación.
 * @param attrs Atributos de la vista, si los hay.
 */
class ChessBoardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    // Paint utilizado para dibujar sobre el canvas
    private val paint = Paint()

    // Instancias de las clases ChessBoard y ChessController que manejan la lógica del juego
    private val board = ChessBoard()
    private val controller = ChessController(board, context)

    // Tamaño del tablero (8x8)
    private val boardSize = 8

    // Tamaño de las celdas calculado dinámicamente
    private var cellSize: Int = 0

    // Colores utilizados para las celdas del tablero
    private val lightColor = Color.LTGRAY
    private val darkColor = Color.DKGRAY
    private val selectedColor = Color.YELLOW

    // Bandera para determinar el turno (si es el turno de las blancas)
    private var isWhiteTurn = true

    // Instancias de ChessTimer para gestionar los temporizadores de los jugadores
    private val whiteTimer = ChessTimer(300000L, ::updateWhiteTimeText, ::onWhiteTimeUp)
    private val blackTimer = ChessTimer(300000L, ::updateBlackTimeText, ::onBlackTimeUp)

    // Textos para mostrar los tiempos de los jugadores
    private var whiteTimeText: String = "05:00"
    private var blackTimeText: String = "05:00"

    init {
        // Configuración de los callbacks para los movimientos de piezas y cambio de turno
        controller.onPieceMoved = { _, _, _, _ -> checkForWin(); invalidate() }
        controller.onTurnChanged = { isWhite -> changeTurn(isWhite) }
    }

    /**
     * Método que se llama para dibujar el contenido en la vista.
     * Dibuja el tablero, las piezas, los indicadores de turno y los temporizadores.
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Calculamos el tamaño de las celdas según el ancho de la vista
        cellSize = width / boardSize

        // Dibujamos el tablero, las piezas, el indicador de turno y los temporizadores
        drawBoard(canvas)
        drawPieces(canvas)
        drawTurnIndicator(canvas)
        drawTimers(canvas)
    }

    /**
     * Dibuja el tablero de ajedrez.
     */
    private fun drawBoard(canvas: Canvas) {
        for (row in 0 until boardSize) {
            for (col in 0 until boardSize) {
                // Establece el color de la celda según si es una celda clara u oscura
                paint.color = getCellColor(row, col)

                // Si la celda está seleccionada, resáltala
                if (row == controller.getSelectedCell().first && col == controller.getSelectedCell().second) {
                    paint.color = selectedColor
                }

                // Dibuja la celda
                drawCell(canvas, row, col)
            }
        }
    }

    /**
     * Devuelve el color de una celda en función de si es clara u oscura.
     */
    private fun getCellColor(row: Int, col: Int): Int {
        return if ((row + col) % 2 == 0) lightColor else darkColor
    }

    /**
     * Dibuja una celda en el canvas.
     */
    private fun drawCell(canvas: Canvas, row: Int, col: Int) {
        val left = col * cellSize
        val top = row * cellSize
        val right = left + cellSize
        val bottom = top + cellSize
        canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
    }

    /**
     * Dibuja las piezas sobre el tablero.
     */
    private fun drawPieces(canvas: Canvas) {
        // Configuramos el tamaño y alineación del texto para las piezas
        paint.textSize = (cellSize * 0.7).toFloat()
        paint.textAlign = Paint.Align.CENTER
        val offsetY = (paint.descent() + paint.ascent()) / 2

        // Dibujamos cada pieza en su celda correspondiente
        for (row in 0 until boardSize) {
            for (col in 0 until boardSize) {
                board.getPieceAt(row, col)?.let {
                    drawPiece(canvas, it, row, col, offsetY)
                }
            }
        }
    }

    /**
     * Dibuja una pieza en una celda especificada.
     */
    private fun drawPiece(canvas: Canvas, piece: ChessPiece, row: Int, col: Int, offsetY: Float) {
        // Determina el color de la pieza (blanca o negra)
        paint.color = if (piece.isWhite()) Color.WHITE else Color.BLACK

        // Obtiene el símbolo Unicode de la pieza
        val unicodePiece = piece.getUnicode()

        // Calcula las coordenadas para centrar la pieza en la celda
        val centerX = (col * cellSize + cellSize / 2).toFloat()
        val centerY = (row * cellSize + cellSize / 2).toFloat() - offsetY

        // Dibuja el texto con el símbolo de la pieza
        canvas.drawText(unicodePiece, centerX, centerY, paint)
    }

    /**
     * Dibuja el indicador de turno en la parte inferior de la vista.
     */
    private fun drawTurnIndicator(canvas: Canvas) {
        paint.textSize = 50f
        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.LEFT

        // Muestra "Turno: Blancas" o "Turno: Negras" según el turno actual
        val message = if (isWhiteTurn) "Turno: Blancas" else "Turno: Negras"
        canvas.drawText(message, 20f, height - 20f, paint)
    }

    /**
     * Dibuja los temporizadores de ambos jugadores en la parte inferior de la vista.
     */
    private fun drawTimers(canvas: Canvas) {
        paint.textSize = 50f
        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.LEFT

        // Muestra el tiempo restante para las blancas y las negras
        canvas.drawText("Blancas: $whiteTimeText", 20f, height - 60f, paint)
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("Negras: $blackTimeText", width - 20f, height - 60f, paint)
    }

    /**
     * Maneja los toques táctiles en la vista, seleccionando celdas y moviendo piezas.
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val col = (event.x / cellSize).toInt()
            val row = (event.y / cellSize).toInt()

            // Si la celda es válida, maneja el toque y redibuja la vista
            if (board.isValidPosition(row, col)) {
                controller.handleCellTouch(row, col)
                invalidate()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    /**
     * Verifica si el juego ha terminado y muestra un mensaje de victoria o derrota.
     */
    private fun checkForWin() {
        when {
            board.hasLost(true) -> showGameOverDialog("¡Las negras ganan!")
            board.hasLost(false) -> showGameOverDialog("¡Las blancas ganan!")
        }
    }

    /**
     * Muestra un cuadro de diálogo cuando el juego ha terminado, con la opción de reiniciar.
     */
    private fun showGameOverDialog(message: String) {
        AlertDialog.Builder(context)
            .setTitle("Fin del juego")
            .setMessage(message)
            .setPositiveButton("Reiniciar") { _, _ -> board.resetBoard(); invalidate() }
            .setCancelable(false)
            .show()
    }

    /**
     * Cambia el turno entre los jugadores y maneja la activación de los temporizadores.
     */
    private fun changeTurn(isWhite: Boolean) {
        isWhiteTurn = isWhite
        if (isWhite) {
            whiteTimer.start()
            blackTimer.cancel()
        } else {
            blackTimer.start()
            whiteTimer.cancel()
        }
        invalidate()
    }

    /**
     * Actualiza el texto que muestra el tiempo restante de las blancas.
     */
    private fun updateWhiteTimeText(time: Long) {
        whiteTimeText = whiteTimer.getFormattedTime()
        invalidate()
    }

    /**
     * Actualiza el texto que muestra el tiempo restante de las negras.
     */
    private fun updateBlackTimeText(time: Long) {
        blackTimeText = blackTimer.getFormattedTime()
        invalidate()
    }

    /**
     * Muestra un mensaje de fin de juego cuando el tiempo de las blancas se ha agotado.
     */
    private fun onWhiteTimeUp() {
        showGameOverDialog("¡Las negras ganan! El tiempo de las blancas ha agotado.")
    }

    /**
     * Muestra un mensaje de fin de juego cuando el tiempo de las negras se ha agotado.
     */
    private fun onBlackTimeUp() {
        showGameOverDialog("¡Las blancas ganan! El tiempo de las negras ha agotado.")
    }
}
