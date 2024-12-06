package com.example.chess

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar ChessBoardView
        val chessBoardView: ChessBoardView = findViewById(R.id.chessBoardView)
        // En este caso no necesitamos hacer más configuraciones
        // ya que el ChessBoardView maneja toda la lógica interna del tablero y las piezas.
    }
}