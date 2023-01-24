package com.example.learnchessopenings

class ChessModel {
    var piecesBox = mutableSetOf<ChessPiece>()
    init {
        reset()
    }

    fun reset() {
        piecesBox.removeAll(piecesBox)

        piecesBox.add(ChessPiece(0, 0,ChessPlayer.WHITE, ChessPieceName.ROOK,R.drawable.wr))
        piecesBox.add(ChessPiece(1,0,ChessPlayer.WHITE, ChessPieceName.KNIGHT,R.drawable.wn))
        piecesBox.add(ChessPiece(2,0,ChessPlayer.WHITE, ChessPieceName.BISHOP,R.drawable.wb))
        piecesBox.add(ChessPiece(3,0,ChessPlayer.WHITE, ChessPieceName.QUEEN,R.drawable.wq))
        piecesBox.add(ChessPiece(4,0,ChessPlayer.WHITE, ChessPieceName.KING,R.drawable.wk))
        piecesBox.add(ChessPiece(5,0,ChessPlayer.WHITE, ChessPieceName.BISHOP,R.drawable.wb))
        piecesBox.add(ChessPiece(6,0,ChessPlayer.WHITE, ChessPieceName.KNIGHT, R.drawable.wn))
        piecesBox.add(ChessPiece(7,0,ChessPlayer.WHITE, ChessPieceName.ROOK,R.drawable.wr))

        piecesBox.add(ChessPiece(0, 7,ChessPlayer.BLACK, ChessPieceName.ROOK,R.drawable.br))
        piecesBox.add(ChessPiece(1,7,ChessPlayer.BLACK, ChessPieceName.KNIGHT,R.drawable.bn))
        piecesBox.add(ChessPiece(2,7,ChessPlayer.BLACK, ChessPieceName.BISHOP,R.drawable.bb))
        piecesBox.add(ChessPiece(3,7,ChessPlayer.BLACK, ChessPieceName.QUEEN,R.drawable.bq))
        piecesBox.add(ChessPiece(4,7,ChessPlayer.BLACK, ChessPieceName.KING,R.drawable.bk))
        piecesBox.add(ChessPiece(5,7,ChessPlayer.BLACK, ChessPieceName.BISHOP,R.drawable.bb))
        piecesBox.add(ChessPiece(6,7,ChessPlayer.BLACK, ChessPieceName.KNIGHT, R.drawable.bn))
        piecesBox.add(ChessPiece(7,7,ChessPlayer.BLACK, ChessPieceName.ROOK,R.drawable.br))

        for( i in 0..7) {
            piecesBox.add(ChessPiece(i,1,ChessPlayer.WHITE, ChessPieceName.PAWN,R.drawable.wp))
            piecesBox.add(ChessPiece(i,6,ChessPlayer.BLACK, ChessPieceName.PAWN,R.drawable.bp))
        }
    }

    fun pieceAt(col: Int, row: Int) : ChessPiece? {
        for(piece in piecesBox) {
            if (col == piece.col && row == piece.row) {
                return piece
            }
        }
        return null
    }

    fun movePiece(fromCol: Int, fromRow: Int, toCol: Int, toRow: Int) {
        val movingPiece = pieceAt(fromCol, fromRow) ?: return
        pieceAt(toCol, toRow)?.let {
            if (it.player == movingPiece.player) return
            piecesBox.remove(it)
        }
        piecesBox.remove(movingPiece)
        piecesBox.add(ChessPiece(toCol, toRow, movingPiece.player, movingPiece.chessPieceName, movingPiece.resID))
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