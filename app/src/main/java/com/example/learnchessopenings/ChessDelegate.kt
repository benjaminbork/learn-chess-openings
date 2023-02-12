package com.example.learnchessopenings

interface ChessDelegate {
    fun pieceAt(square: ChessSquare) : ChessPiece?
    fun movePiece(from: ChessSquare, to: ChessSquare)
    fun toFen(): String
    fun stringToChessSquare(squareString: String) : ChessSquare

    fun getValidMovesForView () : MutableList<ChessMove>

    fun getPuzzleData()
    fun checkPuzzleFetchSuccess () : Boolean


}