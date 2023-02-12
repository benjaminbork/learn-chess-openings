package com.example.learnchessopenings

import android.util.Log
import java.lang.StrictMath.abs

class ChessModel {
    private var piecesBox = mutableSetOf<ChessPiece>()
    private var validMoves = mutableListOf<ChessMove>()
    private var checkingPieces = mutableSetOf<ChessPiece>()
    private var chessLastMove : ChessLastMove? = null
    private var enPassent : ChessPiece? = null
    private var playerToMove : ChessPlayer = ChessPlayer.WHITE
    private var whiteKingLocation : ChessSquare = ChessSquare(4, 0)
    private var blackKingLocation : ChessSquare = ChessSquare(4,7)
    private var isWhiteKingChecked : Boolean = false
    private var isBlackKingChecked : Boolean = false
    init {
        reset()
    }

    fun reset() {
        chessLastMove = null
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
        validMoves.addAll(getAllValidMoves())
    }

    fun resetCheckingPieces() {
        checkingPieces.removeAll(piecesBox)
    }

    fun pieceAt(square: ChessSquare) : ChessPiece? {
        return pieceAt(square.col,square.row)
    }

    private fun pieceAt(col: Int, row: Int) : ChessPiece? {
        for(piece in piecesBox) {
            if (col == piece.col && row == piece.row) {
                return piece
            }
        }
        return null
    }



    fun movePiece(from: ChessSquare, to: ChessSquare) {
        val movingPiece = pieceAt(from)
        val moveString = getMoveString(from, to)
        val move = movingPiece?.let { ChessMove(it,from, to, moveString) }
        if (validMoves.isEmpty()) {
            validMoves.addAll(getAllValidMoves())
        }

        if (move in validMoves) {
            movePiece(from.col, from.row,to.col,to.row)
            chessLastMove = pieceAt(to)?.let { ChessLastMove(it, from, to)}
            chessLastMove?.let {
                if (it.chessPiece.chessPieceName == ChessPieceName.KING) {
                    if (it.chessPiece.player == ChessPlayer.WHITE) {
                        whiteKingLocation = it.to
                    } else {
                        blackKingLocation = it.to
                    }
                }
            }
            switchPlayerToMove()
            validMoves.removeAll(validMoves)
            validMoves.addAll(getAllValidMoves())

            }

        }



    private fun movePiece(fromCol: Int, fromRow: Int, toCol: Int, toRow: Int) {
        if (fromCol == toCol && fromRow == toRow) return
        val movingPiece = pieceAt(fromCol, fromRow) ?: return
        pieceAt(toCol, toRow)?.let {
            if (it.player == movingPiece.player) return
            piecesBox.remove(it)
        }
        piecesBox.remove(movingPiece)
        if (enPassent != null) {
            chessLastMove?.let { piecesBox.remove(it.chessPiece) }
        }
        piecesBox.add(ChessPiece(toCol, toRow, movingPiece.player, movingPiece.chessPieceName, movingPiece.resID))
    }

    private fun getAllValidMoves(): MutableList<ChessMove> {
        enPassent = null
        // 1. generate all moves
        var moves = getAllPossibleMoves()
        val staringPosition = toFen()
        val lastMove = chessLastMove
        // 2. for each move make the move
        // 3. generate all opponents moves
        // 4. for each opponents move, check for checks
        for (i  in moves.size - 1 downTo 0) {
            movePiece(moves[i].from.col, moves[i].from.row, moves[i].to.col, moves[i].to.row)
            if (inCheck()) {
                moves.remove(moves[i])
            }
            loadFEN(staringPosition)
        }

        // 5. if they do attack your king, not a valid move

        return moves

    }
    private fun getAllPossibleMoves(): MutableList<ChessMove> {
        var moves = mutableListOf<ChessMove>()
        for (piece in piecesBox) {
            if (piece.player == playerToMove) {
                for (col in 0..7) {
                    for (row in 0..7) {
                        val to = ChessSquare(col, row)
                        val from = ChessSquare(piece.col, piece.row)
                        val moveString = getMoveString(from, to)
                        if (canPieceMove(from, to)) {
                            moves.add(ChessMove(piece, from, to, moveString))
                        }
                    }
                }
            }
        }

        return moves
    }

    fun getValidMovesForView () : MutableList<ChessMove> {
        return validMoves
    }

    private fun inCheck(): Boolean {
        return if (playerToMove == ChessPlayer.WHITE) {
            squareUnderAttack(whiteKingLocation)
        } else {
            squareUnderAttack(blackKingLocation)
        }
    }

    private fun squareUnderAttack(kingLocation: ChessSquare): Boolean {
        switchPlayerToMove()
        val opponentMoves = getAllPossibleMoves()
        switchPlayerToMove()
        for (move in opponentMoves) {
            if (move.to == kingLocation) {
                return true
            }
        }
        return false
    }

    private fun getMoveString(from: ChessSquare,to: ChessSquare) : String {
        var moveString = ""
        moveString += chessSquareToString(from)
        moveString += chessSquareToString(to)

        return moveString
    }


    fun stringToChessSquare(squareString: String) : ChessSquare {
        var col = -1
        var row = -1
        val stringCol = squareString[0].toString()
        val stringRow = squareString[1].toString()

        when (stringCol) {
            "a" -> {col = 0}
            "b" -> {col = 1}
            "c" -> {col = 2}
            "d" -> {col = 3}
            "e" -> {col = 4}
            "f" -> {col = 5}
            "g" -> {col = 6}
            "h" -> {col = 7}
        }

        when (stringRow) {
            "1" -> {row = 0}
            "2" -> {row = 1}
            "3" -> {row = 2}
            "4" -> {row = 3}
            "5" -> {row = 4}
            "6" -> {row = 5}
            "7" -> {row = 6}
            "8" -> {row = 7}
        }

        return ChessSquare(col,row)

    }

    fun chessSquareToString(chessSquare:ChessSquare) : String {
        val col = chessSquare.col
        val row = chessSquare.row
        var squareString = ""

        when (col) {
            0 -> {squareString += "a"}
            1 -> {squareString += "b"}
            2 -> {squareString += "c"}
            3 -> {squareString += "d"}
            4 -> {squareString += "e"}
            5 -> {squareString += "f"}
            6 -> {squareString += "g"}
            7 -> {squareString += "h"}
        }

        when (row) {
            0 -> {squareString += "1"}
            1 -> {squareString += "2"}
            2 -> {squareString += "3"}
            3 -> {squareString += "4"}
            4 -> {squareString += "5"}
            5 -> {squareString += "6"}
            6 -> {squareString += "7"}
            7 -> {squareString += "8"}
        }

        return squareString

    }

    private fun canKnightMove(from: ChessSquare, to: ChessSquare) : Boolean {

        return (abs(from.col - to.col) == 2 && abs(from.row - to.row) == 1 ||
                abs(from.col - to.col) == 1 && abs(from.row - to.row) == 2)
    }

    private fun canRockMove(from: ChessSquare,to: ChessSquare) : Boolean {

        return ((to.col == from.col && isColBetweenClear(from, to)) ||
            (from.row == to.row && isRowBetweenClear(from, to)))
    }

    private fun canQueenMove(from: ChessSquare, to: ChessSquare) : Boolean {
        return (canRockMove(from, to) || canBishopMove(from, to))
    }

    private fun canBishopMove(from: ChessSquare, to: ChessSquare) : Boolean {
        if (isDiagonalBetweenClear(from, to)) {
            return true
        }
        return false
    }

    private fun canKingMove(from: ChessSquare, to: ChessSquare) : Boolean {
        if (canRockMove(from, to) || canBishopMove(from, to)) {
            return ((abs(from.col - to.col) == 1) &&
                    (abs(from.row - to.row) == 0 || abs(from.row - to.row) == 1)) ||
                    ((abs(from.row - to.row) == 1) &&
                            (abs(from.col - to.col) == 0 || abs(from.col - to.col) == 1))
        }
        return false
    }

    private fun canKingCastle(from: ChessSquare,to: ChessSquare) {

    }

    private fun canPawnMove(from: ChessSquare, to: ChessSquare, chessPiece: ChessPiece): Boolean {
        if (chessPiece.player == ChessPlayer.WHITE && from.row > to.row) return false
        if (chessPiece.player == ChessPlayer.BLACK && from.row < to.row) return false
        if (from.row == 1 && (from.col == to.col)) {
            return (to.row == 2 || to.row == 3) && isColBetweenClear(from, to) && pieceAt(to) == null
        } else if (from.row == 6 && (from.col == to.col)) {
            return (to.row == 5 || to.row == 4) && isColBetweenClear(from, to) && pieceAt(to) == null
        } else if ((isColBetweenClear(from, to) && from.col == to.col) && pieceAt(to) == null ||
            canPawnCapture(from, to) ||
            (canPawnCaptureEnPassent(from, to, chessPiece) && chessLastMove?.to?.col == to.col)){
            return (abs(from.row - to.row) == 1)
        }
        return false
    }

    private fun canPawnCaptureEnPassent(from: ChessSquare, to: ChessSquare, chessPiece: ChessPiece): Boolean {
        var last = chessLastMove ?: return false
        if (last.chessPiece.chessPieceName != ChessPieceName.PAWN) return false
        if (last.chessPiece.player == chessPiece.player) return false



        if (last.chessPiece.player == ChessPlayer.BLACK &&
            last.from.row == 6 && last.to.row == 4 &&
            (from.row == 5 || from.row == 4) &&
            abs(from.col - last.to.col) == 1)
        {
            enPassent = last.chessPiece
            return true
        }
        if (last.chessPiece.player == ChessPlayer.WHITE &&
            last.from.row == 1 && last.to.row == 3 &&
            (from.row == 3 || from.row == 2) &&
            abs(from.col - last.to.col) == 1)
        {
            enPassent = last.chessPiece
            return true
        }

        return false
    }
    private fun canPawnCapture(from: ChessSquare, to: ChessSquare): Boolean {
        val toCapturingPiece = pieceAt(to) ?: return false
        if (toCapturingPiece.player == ChessPlayer.BLACK) {
            if ((abs(from.col - to.col) == abs(from.row - to.row)) &&
                (from.row < to.row)) return true
        }
        if (toCapturingPiece.player == ChessPlayer.WHITE) {
            if ((abs(from.col - to.col) == abs(from.row - to.row)) &&
                (from.row > to.row)) return true
        }
        return false
    }

    private fun isRowBetweenClear(from: ChessSquare,to: ChessSquare) : Boolean {
        if (from.row != to.row) return false
        val gap = abs(from.col - to.col) - 1
        if (gap == 0) return true
        for (i in 1..gap) {
            val nextCol = if (to.col > from.col) from.col + i else from.col - i
            if(pieceAt(nextCol, from.row) != null) return false
        }
        return true
    }

    private fun isColBetweenClear(from: ChessSquare,to: ChessSquare) : Boolean {
        if (from.col != to.col) return false
        val gap = abs(from.row - to.row) - 1
        if (gap == 0) return true
        for (i in 1..gap) {
            val nextRow = if (to.row > from.row) from.row + i else from.row - i
            if(pieceAt(from.col, nextRow) != null) return false
        }
        return true
    }

    private fun isDiagonalBetweenClear (from: ChessSquare,to: ChessSquare) : Boolean {
        if (abs(from.col - to.col) != abs(from.row - to.row)) return false
        val gap = abs(from.col -to.col) - 1
        for (i in 1..gap) {
            val nextCol = if (to.col > from.col) from.col + i else from.col - i
            val nextRow = if (to.row > from.row) from.row + i else from.row - i
            if (pieceAt(nextCol,nextRow) != null) return false
        }
        return true
    }







    private fun isSquareOutsideBoard(to: ChessSquare): Boolean {
        return to.col > 7 || to.col < 0 || to.row > 7 || to.row < 0
    }

    private fun switchPlayerToMove() {
        playerToMove = if (playerToMove == ChessPlayer.WHITE) ChessPlayer.BLACK else ChessPlayer.WHITE
    }


    fun canPieceMove(from: ChessSquare, to: ChessSquare) : Boolean {
        if (from.col == to.col && from.row == to.row) return false
        if (isSquareOutsideBoard(to)) return false
        if (pieceAt(from)?.player == pieceAt(to)?.player) return false
        if(pieceAt(from)?.player != playerToMove) return false
        val movingPiece = pieceAt(from) ?: return false
        return when(movingPiece.chessPieceName) {
            ChessPieceName.KNIGHT -> canKnightMove(from, to)
            ChessPieceName.ROOK -> canRockMove(from, to)
            ChessPieceName.BISHOP -> canBishopMove(from, to)
            ChessPieceName.QUEEN -> canQueenMove(from, to)
            ChessPieceName.KING -> canKingMove(from, to)
            ChessPieceName.PAWN -> canPawnMove(from, to, movingPiece)
        }
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
    private fun loadFEN (fen: String) {
        piecesBox.removeAll(piecesBox)
        var fenString = fen
        fenString = fenString.replace("8","........")
        fenString = fenString.replace("7",".......")
        fenString = fenString.replace("6","......")
        fenString = fenString.replace("5",".....")
        fenString = fenString.replace("4","....")
        fenString = fenString.replace("3","...")
        fenString = fenString.replace("2","..")
        fenString = fenString.replace("1",".")

        val fenRows : List<String> = fenString.split("/")

        for (row in 7 downTo 0) {
            val fenRow = fenRows[7-row]
            for (col in 0..7) {
                val fenCol = fenRow[col]
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