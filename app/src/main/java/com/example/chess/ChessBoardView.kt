package com.example.chess

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AlertDialog

class ChessBoardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint()
    private val board = ChessBoard()
    private val controller = ChessController(board)

    private val boardSize = 8
    private var cellSize: Int = 0

    // Configuración de colores
    private val lightColor = Color.LTGRAY
    private val darkColor = Color.DKGRAY
    private val selectedColor = Color.YELLOW

    // Control de turnos
    private var isWhiteTurn = true

    init {
        controller.onPieceMoved = { _, _, _, _ ->
            checkForWin() // Verificar si alguien ha ganado después de cada movimiento
            invalidate() // Redibujar la vista tras mover una pieza
        }

        controller.onTurnChanged = { isWhite ->
            isWhiteTurn = isWhite
            invalidate() // Actualizar la vista para reflejar el cambio de turno
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        cellSize = width / boardSize

        // Dibujar el tablero
        drawBoard(canvas)
        // Dibujar las piezas
        drawPieces(canvas)
        // Mostrar indicador de turno
        drawTurnIndicator(canvas)
    }

    private fun drawBoard(canvas: Canvas) {
        for (row in 0 until boardSize) {
            for (col in 0 until boardSize) {
                val (selectedRow, selectedCol) = controller.getSelectedCell()
                paint.color = when {
                    row == selectedRow && col == selectedCol -> selectedColor
                    (row + col) % 2 == 0 -> lightColor
                    else -> darkColor
                }
                val left = col * cellSize
                val top = row * cellSize
                val right = left + cellSize
                val bottom = top + cellSize
                canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
            }
        }
    }

    private fun drawPieces(canvas: Canvas) {
        paint.textSize = (cellSize * 0.7).toFloat() // Tamaño de la fuente para las piezas
        paint.textAlign = Paint.Align.CENTER
        val offsetY = (paint.descent() + paint.ascent()) / 2 // Ajuste vertical

        for (row in 0 until boardSize) {
            for (col in 0 until boardSize) {
                val piece = board.getPieceAt(row, col)
                piece?.let {
                    paint.color = if (it.isWhite()) Color.WHITE else Color.BLACK
                    val unicodePiece = it.getUnicode()
                    val centerX = (col * cellSize + cellSize / 2).toFloat()
                    val centerY = (row * cellSize + cellSize / 2).toFloat() - offsetY
                    canvas.drawText(unicodePiece, centerX, centerY, paint)
                }
            }
        }
    }

    private fun drawTurnIndicator(canvas: Canvas) {
        paint.textSize = 50f
        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.LEFT
        val message = if (isWhiteTurn) "Turno: Blancas" else "Turno: Negras"
        canvas.drawText(message, 20f, height - 20f, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val col = (event.x / cellSize).toInt()
            val row = (event.y / cellSize).toInt()

            if (board.isValidPosition(row, col)) {
                controller.handleCellTouch(row, col)
                invalidate() // Redibujar la vista
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun checkForWin() {
        when {
            board.hasLost(true) -> showGameOverDialog("¡Las negras ganan!")
            board.hasLost(false) -> showGameOverDialog("¡Las blancas ganan!")
        }
    }

    private fun showGameOverDialog(message: String) {
        AlertDialog.Builder(context)
            .setTitle("Fin del juego")
            .setMessage(message)
            .setPositiveButton("Reiniciar") { _, _ -> board.resetBoard(); invalidate() }
            .setCancelable(false)
            .show()
    }
}



