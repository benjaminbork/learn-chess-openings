package com.example.learnchessopenings

interface ChessDelegate {
    fun pieceAt(col: Int, row: Int) : ChessPiece?
}