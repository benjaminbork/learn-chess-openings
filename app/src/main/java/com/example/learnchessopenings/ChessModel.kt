package com.example.learnchessopenings

import android.util.Log

class ChessModel {
    var piecesBox = mutableSetOf<ChessPiece>()
    init {
        // reset()
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
        if (fromCol == toCol && fromRow == toRow) return
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
     fun toFen(): String {
         var fen = ""
         var row = 4
         for (row in 7 downTo 0) {
             for (col in 0..7) {
                 val piece = pieceAt(col, row)
                 if (piece == null) {
                     fen += "."
                 } else {
                     fen += when (piece.chessPieceName) {
                         ChessPieceName.KING -> {
                             if (piece.player == ChessPlayer.WHITE) "K" else "k"
                         }
                         ChessPieceName.QUEEN -> {
                             if (piece.player == ChessPlayer.WHITE) "Q" else "q"
                         }
                         ChessPieceName.ROOK -> {
                             if (piece.player == ChessPlayer.WHITE) "R" else "r"
                         }
                         ChessPieceName.BISHOP -> {
                             if (piece.player == ChessPlayer.WHITE) "B" else "b"
                         }
                         ChessPieceName.KNIGHT -> {
                             if (piece.player == ChessPlayer.WHITE) "N" else "n"
                         }
                         ChessPieceName.PAWN -> {
                             if (piece.player == ChessPlayer.WHITE) "P" else "p"
                         }
                     }
                 }
             }
             fen = fen.replace("........","8")
             fen = fen.replace(".......","7")
             fen = fen.replace("......","6")
             fen = fen.replace(".....","5")
             fen = fen.replace("....","4")
             fen = fen.replace("...","3")
             fen = fen.replace("..","2")
             fen = fen.replace(".","1")
             if (row != 0) {
                 fen += "/"
             }
         }
         return fen
    }
    fun loadFEN (fen: String) {
        piecesBox.removeAll(piecesBox)
        var fen = fen
        fen = fen.replace("8","........")
        fen = fen.replace("7",".......")
        fen = fen.replace("6","......")
        fen = fen.replace("5",".....")
        fen = fen.replace("4","....")
        fen = fen.replace("3","...")
        fen = fen.replace("2","..")
        fen = fen.replace("1",".")

        var fenRows : List<String> = fen.split("/")
        Log.d(TAG, fenRows.toString())

        for (row in 7 downTo 0) {
            var fenRow = fenRows[7-row]
            for (col in 0..7) {
                var fenCol = fenRow[col]
                when (fenCol.toString()) {
                    "K" -> {
                        piecesBox.add(ChessPiece(col,row,ChessPlayer.WHITE, ChessPieceName.KING,R.drawable.wk))
                    }
                    "k" -> {
                        piecesBox.add(ChessPiece(col,row,ChessPlayer.BLACK, ChessPieceName.KING,R.drawable.bk))
                    }
                    "Q" -> {
                        piecesBox.add(ChessPiece(col,row,ChessPlayer.WHITE, ChessPieceName.QUEEN,R.drawable.wq))
                    }
                    "q" -> {
                        piecesBox.add(ChessPiece(col,row,ChessPlayer.BLACK, ChessPieceName.QUEEN,R.drawable.bq))
                    }
                    "R" -> {
                        piecesBox.add(ChessPiece(col,row,ChessPlayer.WHITE, ChessPieceName.ROOK,R.drawable.wr))
                    }
                    "r" -> {
                        piecesBox.add(ChessPiece(col,row,ChessPlayer.BLACK, ChessPieceName.ROOK,R.drawable.br))
                    }
                    "B" -> {
                        piecesBox.add(ChessPiece(col,row,ChessPlayer.WHITE, ChessPieceName.BISHOP,R.drawable.wb))
                    }
                    "b" -> {
                        piecesBox.add(ChessPiece(col,row,ChessPlayer.BLACK, ChessPieceName.BISHOP,R.drawable.bb))
                    }
                    "N" -> {
                        piecesBox.add(ChessPiece(col,row,ChessPlayer.WHITE, ChessPieceName.KNIGHT,R.drawable.wn))
                    }
                    "n" -> {
                        piecesBox.add(ChessPiece(col,row,ChessPlayer.BLACK, ChessPieceName.KNIGHT,R.drawable.bn))
                    }
                    "P" -> {
                        piecesBox.add(ChessPiece(col,row,ChessPlayer.WHITE, ChessPieceName.PAWN,R.drawable.wp))
                    }
                    "p" -> {
                        piecesBox.add(ChessPiece(col,row,ChessPlayer.BLACK, ChessPieceName.PAWN,R.drawable.bp))
                    }
                }

            }
        }


    }


}