package com.example.learnchessopenings

class ChessModel {
    var piecesBox = mutableSetOf<ChessPiece>()
    init {
        reset()
    }

    fun reset() {
       piecesBox.removeAll(piecesBox)
       var playerColor = ChessPlayer.WHITE
       var playerRow = 0
       for(i in 0..1) {
           piecesBox.add(ChessPiece(0, playerRow,playerColor, ChessPieceName.ROOK))
           piecesBox.add(ChessPiece(1,playerRow,playerColor, ChessPieceName.KNIGHT))
           piecesBox.add(ChessPiece(2,playerRow,playerColor, ChessPieceName.BISHOP))
           piecesBox.add(ChessPiece(3,playerRow,playerColor, ChessPieceName.QUEEN))
           piecesBox.add(ChessPiece(4,playerRow,playerColor, ChessPieceName.KING))
           piecesBox.add(ChessPiece(5,playerRow,playerColor, ChessPieceName.BISHOP))
           piecesBox.add(ChessPiece(6,playerRow,playerColor, ChessPieceName.KNIGHT))
           piecesBox.add(ChessPiece(7,playerRow,playerColor, ChessPieceName.ROOK))
           for( i in 0..7) {
               if(playerColor == ChessPlayer.WHITE) {
                   piecesBox.add(ChessPiece(i,1,playerColor, ChessPieceName.PAWN))
               } else {
                   piecesBox.add(ChessPiece(i,6,playerColor, ChessPieceName.PAWN))
               }
           }
           playerColor = ChessPlayer.BLACK
           playerRow = 7
       }


       // black pieces
   }
    private fun pieceAt(col: Int, row: Int) : ChessPiece? {
        for(piece in piecesBox) {
            if (col == piece.col && row == piece.row) {
                return piece
            }
        }
        return null
    }
    override fun toString(): String {
        var description = " \n"
        for (row in 7 downTo 0) {
            description += "$row"
            for(col in 0..7) {
                val piece = pieceAt(col, row)
                if (piece == null) {
                    description += " ."
                } else {
                    description += " "
                    description += when (piece.chessPieceName) {
                        ChessPieceName.KING -> {
                            if (piece.player == ChessPlayer.WHITE) "k" else "K"
                        }
                        ChessPieceName.QUEEN -> {
                            if (piece.player == ChessPlayer.WHITE) "q" else "Q"
                        }
                        ChessPieceName.ROOK -> {
                            if (piece.player == ChessPlayer.WHITE) "r" else "R"
                        }
                        ChessPieceName.BISHOP -> {
                            if (piece.player == ChessPlayer.WHITE) "b" else "B"
                        }
                        ChessPieceName.KNIGHT -> {
                            if (piece.player == ChessPlayer.WHITE) "n" else "N"
                        }
                        ChessPieceName.PAWN -> {
                            if (piece.player == ChessPlayer.WHITE) "p" else "P"
                        }
                    }
                }

            }
            description += "\n"
        }
        description += "  0 1 2 3 4 5 6 7"
        return description
    }
}