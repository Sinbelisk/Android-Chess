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
    private val controller = ChessController(board, context)

    private val boardSize = 8
    private var cellSize: Int = 0

    private val lightColor = Color.LTGRAY
    private val darkColor = Color.DKGRAY
    private val selectedColor = Color.YELLOW

    private var isWhiteTurn = true

    private val whiteTimer = ChessTimer(300000L, ::updateWhiteTimeText, ::onWhiteTimeUp)
    private val blackTimer = ChessTimer(300000L, ::updateBlackTimeText, ::onBlackTimeUp)

    private var whiteTimeText: String = "05:00"
    private var blackTimeText: String = "05:00"

    init {

        controller.onPieceMoved = { _, _, _, _ -> checkForWin(); invalidate() }
        controller.onTurnChanged = { isWhite -> changeTurn(isWhite) }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        cellSize = width / boardSize

        drawBoard(canvas)
        drawPieces(canvas)
        drawTurnIndicator(canvas)
        drawTimers(canvas)
    }

    private fun drawBoard(canvas: Canvas) {
        for (row in 0 until boardSize) {
            for (col in 0 until boardSize) {
                paint.color = getCellColor(row, col)  // Color de fondo de la celda
                if (row == controller.getSelectedCell().first && col == controller.getSelectedCell().second) {
                    paint.color = selectedColor  // Si la celda es seleccionada, resáltala
                }
                drawCell(canvas, row, col)
            }
        }
    }

    private fun getCellColor(row: Int, col: Int): Int {
        return if ((row + col) % 2 == 0) lightColor else darkColor
    }

    private fun drawCell(canvas: Canvas, row: Int, col: Int) {
        val left = col * cellSize
        val top = row * cellSize
        val right = left + cellSize
        val bottom = top + cellSize
        canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
    }

    private fun drawPieces(canvas: Canvas) {
        paint.textSize = (cellSize * 0.7).toFloat()
        paint.textAlign = Paint.Align.CENTER
        val offsetY = (paint.descent() + paint.ascent()) / 2

        for (row in 0 until boardSize) {
            for (col in 0 until boardSize) {
                val piece = board.getPieceAt(row, col)
                piece?.let {
                    drawPiece(canvas, it, row, col, offsetY)
                }
            }
        }
    }

    private fun drawPiece(canvas: Canvas, piece: ChessPiece, row: Int, col: Int, offsetY: Float) {
        paint.color = if (piece.isWhite()) Color.WHITE else Color.BLACK
        val unicodePiece = piece.getUnicode()
        val centerX = (col * cellSize + cellSize / 2).toFloat()
        val centerY = (row * cellSize + cellSize / 2).toFloat() - offsetY
        canvas.drawText(unicodePiece, centerX, centerY, paint)
    }

    private fun drawTurnIndicator(canvas: Canvas) {
        paint.textSize = 50f
        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.LEFT
        val message = if (isWhiteTurn) "Turno: Blancas" else "Turno: Negras"
        canvas.drawText(message, 20f, height - 20f, paint)
    }

    private fun drawTimers(canvas: Canvas) {
        paint.textSize = 50f
        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.LEFT
        canvas.drawText("Blancas: $whiteTimeText", 20f, height - 60f, paint)

        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("Negras: $blackTimeText", width - 20f, height - 60f, paint)
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

    private fun updateWhiteTimeText(time: Long) {
        whiteTimeText = whiteTimer.getFormattedTime()
        invalidate()
    }

    private fun updateBlackTimeText(time: Long) {
        blackTimeText = blackTimer.getFormattedTime()
        invalidate()
    }

    private fun onWhiteTimeUp() {
        showGameOverDialog("¡Las negras ganan! El tiempo de las blancas ha agotado.")
    }

    private fun onBlackTimeUp() {
        showGameOverDialog("¡Las blancas ganan! El tiempo de las negras ha agotado.")
    }
}