package com.example.chess

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class ChessBoardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint()
    private val board = ChessBoard()

    private val boardSize = 8
    private var cellSize: Int = 0

    // Configuración de colores
    private val lightColor = Color.LTGRAY
    private val darkColor = Color.DKGRAY
    private val selectedColor = Color.YELLOW

    // Estado de interacción
    private var selectedRow: Int? = null
    private var selectedCol: Int? = null

    // Listener para eventos de interacción
    var onPieceMoved: ((fromRow: Int, fromCol: Int, toRow: Int, toCol: Int) -> Unit)? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        cellSize = width / boardSize

        // Dibujar el tablero
        drawBoard(canvas)
        // Dibujar las piezas
        drawPieces(canvas)
    }

    private fun drawBoard(canvas: Canvas) {
        for (row in 0 until boardSize) {
            for (col in 0 until boardSize) {
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

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Asegurar que el tablero sea cuadrado
        val size = MeasureSpec.getSize(widthMeasureSpec).coerceAtMost(MeasureSpec.getSize(heightMeasureSpec))
        setMeasuredDimension(size, size)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val col = (event.x / cellSize).toInt()
            val row = (event.y / cellSize).toInt()

            if (board.isValidPosition(row, col)) {
                handleCellTouch(row, col)
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun handleCellTouch(row: Int, col: Int) {
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

        invalidate() // Redibujar la vista
    }
}

