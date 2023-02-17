package com.example.learnchessopenings

import android.util.Log
import retrofit2.Response
import java.lang.StrictMath.abs

class ChessModel {
    // general game variables
    private var gameOn = true
    private var isPuzzle = false
    private var isOpening = false
    private var piecesBox = mutableSetOf<ChessPiece>()
    private var validMoves = mutableListOf<ChessMove>()
    private var playerToMove : ChessPlayer = ChessPlayer.WHITE
    private var lastMove : ChessLastMove? = null
    private var checkingFutureMoves = false
    private var previousPosition = ""

    // pawn variables
    private var enPassant : ChessPiece? = null

    // king variables
    private var whiteKingLocation : ChessSquare = ChessSquare(4, 0)
    private var blackKingLocation : ChessSquare = ChessSquare(4,7)

    // castling variables
    private var hasWhiteKingMoved = false
    private var hasBlackKingMoved = false
    private var hasShortWhiteRookMoved = false
    private var hasLongWhiteRookMoved = false
    private var hasShortBlackRookMoved = false
    private var hasLongBlackRookMoved = false

    // isPuzzle variables
    private var solution = ""
    private var puzzlePgn = ""
    private var puzzleStartingPosition = ""
    private var puzzlePlayerToMove : ChessPlayer = ChessPlayer.WHITE
    private var puzzlePositions = mutableListOf<String>()
    private var puzzlePositionIndex = 0
    init {
        reset()
    }

    fun reset() {
        lastMove = null
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
            // check for enPassant
            if (move?.chessPiece?.chessPieceName == ChessPieceName.PAWN) {
                if (canPawnMove(move.from, move.to, move.chessPiece)) {
                    lastMove?.let {
                        if (enPassant != null) {
                            piecesBox.remove(it.chessPiece)
                        }
                    }
                }
            }
            // move piece
            movePiece(from.col, from.row,to.col,to.row)
            // set last move and more checks
            lastMove = pieceAt(to)?.let { ChessLastMove(it, from, to)}
            lastMove?.let {
                // check if last move was a king move
                if (it.chessPiece.chessPieceName == ChessPieceName.KING) {
                    if (it.chessPiece.player == ChessPlayer.WHITE) {
                        whiteKingLocation = it.to
                        hasWhiteKingMoved = true
                    } else {
                        blackKingLocation = it.to
                        hasBlackKingMoved = true
                    }
                }
                // check if last move was a rook move
                if (it.chessPiece.chessPieceName == ChessPieceName.ROOK && playerToMove == ChessPlayer.WHITE) {
                    if (it.from == ChessSquare(0,0)) {
                        hasLongWhiteRookMoved = true
                    }
                    if (it.from == ChessSquare(7,0)) {
                        hasShortWhiteRookMoved = true
                    }
                }
                if (it.chessPiece.chessPieceName == ChessPieceName.ROOK && playerToMove == ChessPlayer.BLACK) {
                    if (it.from == ChessSquare(0,7)) {
                        hasLongBlackRookMoved = true
                    }
                    if (it.from == ChessSquare(7,7)) {
                        hasShortBlackRookMoved = true
                    }
                }
                // check for short castling
                if (it.chessPiece.chessPieceName == ChessPieceName.KING && playerToMove == ChessPlayer.WHITE
                    && it.from == ChessSquare(4,0) && it.to == ChessSquare(6,0)) {
                    movePiece(7, 0, 5,0)
                }
                if (it.chessPiece.chessPieceName == ChessPieceName.KING && playerToMove == ChessPlayer.BLACK
                    && it.from == ChessSquare(4,7) && it.to == ChessSquare(6,7)) {
                    movePiece(7, 7, 5,7)
                }

                // check for long castling
                if (it.chessPiece.chessPieceName == ChessPieceName.KING && playerToMove == ChessPlayer.WHITE
                    && it.from == ChessSquare(4,0) && it.to == ChessSquare(2,0)) {
                    movePiece(0, 0, 3,0)
                }
                if (it.chessPiece.chessPieceName == ChessPieceName.KING && playerToMove == ChessPlayer.BLACK
                    && it.from == ChessSquare(4,7) && it.to == ChessSquare(2,7)) {
                    movePiece(0, 7, 3,7)
                }

                }
            switchPlayerToMove()
            previousPosition = toFen()
            validMoves.removeAll(validMoves)
            validMoves.addAll(getAllValidMoves())
            }

        }

    private fun movePieceWithoutValidation(from: ChessSquare, to: ChessSquare) {
        val movingPiece = pieceAt(from)
        val moveString = getMoveString(from, to)
        val move = movingPiece?.let { ChessMove(it,from, to, moveString) }

        if (move?.chessPiece?.chessPieceName == ChessPieceName.PAWN) {
                if (canPawnMove(move.from, move.to, move.chessPiece)) {
                    lastMove?.let {
                        if (enPassant != null) {
                            piecesBox.remove(it.chessPiece)
                        }
                    }
                }
            }
            // move piece
            movePiece(from.col, from.row,to.col,to.row)
            // set last move and more checks
            lastMove = pieceAt(to)?.let { ChessLastMove(it, from, to)}
            lastMove?.let {
                // check if last move was a king move
                if (it.chessPiece.chessPieceName == ChessPieceName.KING) {
                    if (it.chessPiece.player == ChessPlayer.WHITE) {
                        whiteKingLocation = it.to
                        hasWhiteKingMoved = true
                    } else {
                        blackKingLocation = it.to
                        hasBlackKingMoved = true
                    }
                }
                // check if last move was a rook move
                if (it.chessPiece.chessPieceName == ChessPieceName.ROOK && playerToMove == ChessPlayer.WHITE) {
                    if (it.from == ChessSquare(0,0)) {
                        hasLongWhiteRookMoved = true
                    }
                    if (it.from == ChessSquare(7,0)) {
                        hasShortWhiteRookMoved = true
                    }
                }
                if (it.chessPiece.chessPieceName == ChessPieceName.ROOK && playerToMove == ChessPlayer.BLACK) {
                    if (it.from == ChessSquare(0,7)) {
                        hasLongBlackRookMoved = true
                    }
                    if (it.from == ChessSquare(7,7)) {
                        hasShortBlackRookMoved = true
                    }
                }
                // check for short castling
                if (it.chessPiece.chessPieceName == ChessPieceName.KING && playerToMove == ChessPlayer.WHITE
                    && it.from == ChessSquare(4,0) && it.to == ChessSquare(6,0)) {
                    movePiece(7, 0, 5,0)
                }
                if (it.chessPiece.chessPieceName == ChessPieceName.KING && playerToMove == ChessPlayer.BLACK
                    && it.from == ChessSquare(4,7) && it.to == ChessSquare(6,7)) {
                    movePiece(7, 7, 5,7)
                }

                // check for long castling
                if (it.chessPiece.chessPieceName == ChessPieceName.KING && playerToMove == ChessPlayer.WHITE
                    && it.from == ChessSquare(4,0) && it.to == ChessSquare(2,0)) {
                    movePiece(0, 0, 3,0)
                }
                if (it.chessPiece.chessPieceName == ChessPieceName.KING && playerToMove == ChessPlayer.BLACK
                    && it.from == ChessSquare(4,7) && it.to == ChessSquare(2,7)) {
                    movePiece(0, 7, 3,7)
                }

            }
            switchPlayerToMove()



    }


    private fun movePiece(fromCol: Int, fromRow: Int, toCol: Int, toRow: Int) {
        if (fromCol == toCol && fromRow == toRow) return
        val movingPiece = pieceAt(fromCol, fromRow) ?: return
        pieceAt(toCol, toRow)?.let {
            if (it.player == movingPiece.player) return
            piecesBox.remove(it)
        }
        piecesBox.remove(movingPiece)
        piecesBox.add(ChessPiece(toCol, toRow, movingPiece.player, movingPiece.chessPieceName, movingPiece.resID))
    }

    private fun getAllValidMoves(): MutableList<ChessMove> {
        enPassant = null
        checkingFutureMoves = true
        val moves = getAllPossibleMoves()
        val staringPosition = toFen()
        for (i  in moves.size - 1 downTo 0) {
            if (moves[i].chessPiece.chessPieceName != ChessPieceName.KING) {
                movePiece(moves[i].from.col, moves[i].from.row, moves[i].to.col, moves[i].to.row)
                if (inCheck()) {
                    moves.remove(moves[i])
                }
                loadFEN(staringPosition)
            } else if (moves[i].chessPiece.chessPieceName == ChessPieceName.KING){
                if (squareUnderAttack(moves[i].to, moves[i].chessPiece)) {
                    moves.remove(moves[i])
                }
            }

        }
        checkingFutureMoves = false
        return moves

    }
    private fun getAllPossibleMoves(): MutableList<ChessMove> {
        val moves = mutableListOf<ChessMove>()
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

    private fun chessSquareToString(chessSquare:ChessSquare) : String {
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
        if (canKingShortCastling(from, to)) {
           return true
            }

        if (canKingLongCastling(from, to)) {
            return true
        }
        if ((canRockMove(from, to) || canBishopMove(from, to))) {
            return (((abs(from.col - to.col) == 1) &&
                    (abs(from.row - to.row) == 0 || abs(from.row - to.row) == 1))
                    || ((abs(from.row - to.row) == 1) && (abs(from.col - to.col) == 0 || abs(from.col - to.col) == 1)))
        }
        return false
    }

    private fun canKingShortCastling (from: ChessSquare, to: ChessSquare) : Boolean {
        val whiteShortCastlingSafeSquares = mutableListOf<ChessSquare>()
        whiteShortCastlingSafeSquares.add(ChessSquare(4,0))
        whiteShortCastlingSafeSquares.add(ChessSquare(5,0))
        whiteShortCastlingSafeSquares.add(ChessSquare(6,0))

        val blackShortCastlingSafeSquares = mutableListOf<ChessSquare>()
        blackShortCastlingSafeSquares.add(ChessSquare(4,7))
        blackShortCastlingSafeSquares.add(ChessSquare(5,7))
        blackShortCastlingSafeSquares.add(ChessSquare(6,7))

        if ((ChessSquare(to.col, to.row) == ChessSquare(6,0))
            && hasKingShortCastlingRights()
            && isRowBetweenClear(from, ChessSquare(7,0))
            && !squaresUnderAttack(whiteShortCastlingSafeSquares)
            && playerToMove == ChessPlayer.WHITE
                ) {
            return true
        }
        if ((ChessSquare(to.col, to.row) == ChessSquare(6,7))
            && hasKingShortCastlingRights()
            && isRowBetweenClear(from, ChessSquare(7,7))
            && !squaresUnderAttack(blackShortCastlingSafeSquares)
            && playerToMove == ChessPlayer.BLACK
        ) {
            return true
        }
        return false
    }

    private fun canKingLongCastling (from: ChessSquare, to: ChessSquare) : Boolean {
        val whiteLongCastlingSafeSquares = mutableListOf<ChessSquare>()
        whiteLongCastlingSafeSquares.add(ChessSquare(4,0))
        whiteLongCastlingSafeSquares.add(ChessSquare(3,0))
        whiteLongCastlingSafeSquares.add(ChessSquare(2,0))

        val blackLongCastlingSafeSquares = mutableListOf<ChessSquare>()
        blackLongCastlingSafeSquares.add(ChessSquare(4,7))
        blackLongCastlingSafeSquares.add(ChessSquare(3,7))
        blackLongCastlingSafeSquares.add(ChessSquare(2,7))

        if ((ChessSquare(to.col, to.row) == ChessSquare(2,0))
            && hasKingLongCastlingRights()
            && isRowBetweenClear(from, ChessSquare(0,0))
            && !squaresUnderAttack(whiteLongCastlingSafeSquares)
            && playerToMove == ChessPlayer.WHITE
        ) {
            return true
        }
        if ((ChessSquare(to.col, to.row) == ChessSquare(2,7))
            && hasKingLongCastlingRights()
            && isRowBetweenClear(from, ChessSquare(0,7))
            && !squaresUnderAttack(blackLongCastlingSafeSquares)
            && playerToMove == ChessPlayer.BLACK
        ) {
            return true
        }
        return false
    }



    private fun hasKingShortCastlingRights() : Boolean {
        return if (playerToMove == ChessPlayer.WHITE) {
            !hasShortWhiteRookMoved && !hasWhiteKingMoved
        } else {
            !hasShortBlackRookMoved && !hasBlackKingMoved
        }
    }

    private fun hasKingLongCastlingRights() : Boolean {
        return if (playerToMove == ChessPlayer.WHITE) {
            !hasLongWhiteRookMoved && !hasWhiteKingMoved
        } else {
            !hasLongBlackRookMoved && !hasBlackKingMoved
        }
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
            (canPawnCaptureEnPassant(from, chessPiece) && lastMove?.to?.col == to.col)){
            return (abs(from.row - to.row) == 1)
        }
        return false
    }

    private fun canPawnCaptureEnPassant(from: ChessSquare, chessPiece: ChessPiece): Boolean {

        val last = lastMove ?: return false
        if (last.chessPiece.chessPieceName != ChessPieceName.PAWN) return false
        if (last.chessPiece.player == chessPiece.player) return false



        if (last.chessPiece.player == ChessPlayer.BLACK &&
            last.from.row == 6 && last.to.row == 4 &&
            (from.row == 5 || from.row == 4) &&
            abs(from.col - last.to.col) == 1)
        {
            if (checkingFutureMoves) return true
            enPassant = last.chessPiece
            return true
        }
        if (last.chessPiece.player == ChessPlayer.WHITE &&
            last.from.row == 1 && last.to.row == 3 &&
            (from.row == 3 || from.row == 2) &&
            abs(from.col - last.to.col) == 1)
        {
            if (checkingFutureMoves) return true
            enPassant = last.chessPiece
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

    private fun inCheck(): Boolean {
        return if (playerToMove == ChessPlayer.WHITE) {
            futureSquareUnderAttack(whiteKingLocation)
        } else {
            futureSquareUnderAttack(blackKingLocation)
        }
    }

    private fun futureSquareUnderAttack(chessSquare: ChessSquare): Boolean {
        switchPlayerToMove()
        val opponentMoves = getAllPossibleMoves()
        switchPlayerToMove()
        for (move in opponentMoves) {
            if (move.to == chessSquare) {
                return true
            }
        }
        return false
    }

    private fun squareUnderAttack(chessSquare: ChessSquare, chessPiece: ChessPiece): Boolean {
        val startingPosition = toFen()
        movePiece(chessPiece.col, chessPiece.row, chessSquare.col, chessSquare.row)
        switchPlayerToMove()
        for (piece in piecesBox) {
            if (piece.player == playerToMove) {
                if (canPieceMove(ChessSquare(piece.col, piece.row), chessSquare)) {
                    switchPlayerToMove()
                    loadFEN(startingPosition)
                    return true
                }
            }
        }
        switchPlayerToMove()
        loadFEN(startingPosition)
        return false
    }


    private fun squaresUnderAttack(chessSquares: MutableList<ChessSquare>): Boolean {
        switchPlayerToMove()
        for (chessSquare in chessSquares) {
            for (piece in piecesBox) {
                if (piece.player == playerToMove) {
                    if (canPieceMove(ChessSquare(piece.col, piece.row), chessSquare)) {
                        switchPlayerToMove()
                        return true
                    }
                }
            }
        }
        switchPlayerToMove()
        return false
    }





    private fun isSquareOutsideBoard(to: ChessSquare): Boolean {
        return to.col > 7 || to.col < 0 || to.row > 7 || to.row < 0
    }

    private fun switchPlayerToMove() {
        playerToMove = if (playerToMove == ChessPlayer.WHITE) ChessPlayer.BLACK else ChessPlayer.WHITE
    }


    private fun canPieceMove(from: ChessSquare, to: ChessSquare) : Boolean {
        if (!gameOn) return false
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
    fun loadFEN (fen: String) {
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

    private fun stringToCol (string: String) : Int? {
        return when (string) {
            "a" -> 0
            "b" -> 1
            "c" -> 2
            "d" -> 3
            "e" -> 4
            "f" -> 5
            "g" -> 6
            "h" -> 7
            else -> null
        }
    }

    private fun stringToRow (string: String) : Int? {
        return when (string) {
            "1" -> 0
            "2" -> 1
            "3" -> 2
            "4" -> 3
            "5" -> 4
            "6" -> 5
            "7" -> 6
            "8" -> 7
            else -> null
        }
    }

    fun loadPuzzleStartingPosition() {
        if (puzzlePgn.isEmpty()) return
        val pgnMoves = puzzlePgn.split(" ")
        Log.d(TAG, pgnMoves.toString())


        var i = 0

        for (pgnMove in pgnMoves) {
            val legalMoves = getAllValidMoves()
            i += 1
            // understand the meaning behind the pgn move
            var pgnMoveString = pgnMove
            var movingPiece : ChessPieceName? = null
            var movingPieceCol : Int? = null
            var movingPieceRow : Int? = null
            var toSquare : ChessSquare? = null
            var moved = false


            pgnMoveString = pgnMoveString.replace("+", "")
            pgnMoveString = pgnMoveString.replace("x", "")

            Log.d(TAG,"-------------")
            Log.d(TAG,"$i")
            Log.d(TAG,"String:$pgnMoveString")

            Log.d(TAG,"first char:${pgnMoveString[0].toString()}")

            // pawn moves
            if (pgnMoveString.length == 2) {
                movingPiece = ChessPieceName.PAWN
                toSquare = stringToChessSquare(pgnMoveString)
            }
            if ((pgnMoveString.length == 3)
                && (pgnMoveString[0].toString() == "a"
                        || pgnMoveString[0].toString() == "b"
                        || pgnMoveString[0].toString() == "c"
                        || pgnMoveString[0].toString() == "d"
                        || pgnMoveString[0].toString() == "e"
                        || pgnMoveString[0].toString() == "f"
                        || pgnMoveString[0].toString() == "g"
                        || pgnMoveString[0].toString() == "h"
                        )
            ) {
                movingPiece = ChessPieceName.PAWN
                toSquare = stringToChessSquare(pgnMoveString.substring(1,3))
                movingPieceCol = stringToCol(pgnMoveString[0].toString())
            }

            // bishop moves
            if ((pgnMoveString.length == 3)
                && (pgnMoveString[0].toString() == "B")) {
                movingPiece = ChessPieceName.BISHOP
                toSquare = stringToChessSquare(pgnMoveString.substring(1,3))


            }

            // queen moves
            if ((pgnMoveString.length == 3)
                && (pgnMoveString[0].toString() == "Q")) {
                movingPiece = ChessPieceName.QUEEN
                toSquare = stringToChessSquare(pgnMoveString.substring(1,3))
            }

            // king moves
            if ((pgnMoveString.length == 3)
                && (pgnMoveString[0].toString() == "K")) {
                movingPiece = ChessPieceName.KING
                toSquare = stringToChessSquare(pgnMoveString.substring(1,3))
            }

            if (pgnMoveString == "O-O") {
                movingPiece = ChessPieceName.KING
                toSquare = if (playerToMove == ChessPlayer.WHITE) {
                    ChessSquare(6,0)
                } else {
                    ChessSquare(6,7)
                }
            }

            if (pgnMoveString == "O-O-O") {
                movingPiece = ChessPieceName.KING
                toSquare = if (playerToMove == ChessPlayer.WHITE) {
                    ChessSquare(2,0)
                } else {
                    ChessSquare(2,7)
                }
            }



            // rook moves
            if ((pgnMoveString.length == 3)
                && (pgnMoveString[0].toString() == "R")) {
                movingPiece = ChessPieceName.ROOK
                toSquare = stringToChessSquare(pgnMoveString.substring(1,3))
            }

            if ((pgnMoveString.length == 4)
                && (pgnMoveString[0].toString() == "R")
                && (pgnMoveString[1].toString() == "a"
                        || pgnMoveString[1].toString() == "b"
                        || pgnMoveString[1].toString() == "c"
                        || pgnMoveString[1].toString() == "d"
                        || pgnMoveString[1].toString() == "e"
                        || pgnMoveString[1].toString() == "f"
                        || pgnMoveString[1].toString() == "g"
                        || pgnMoveString[1].toString() == "h"
                        )
            ) {
                movingPiece = ChessPieceName.ROOK
                toSquare = stringToChessSquare(pgnMoveString.substring(2,4))
                movingPieceCol = stringToCol(pgnMoveString[1].toString())
            }

            if ((pgnMoveString.length == 4)
                && (pgnMoveString[0].toString() == "R")
                && (pgnMoveString[1].toString() == "1"
                        || pgnMoveString[1].toString() == "2"
                        || pgnMoveString[1].toString() == "3"
                        || pgnMoveString[1].toString() == "4"
                        || pgnMoveString[1].toString() == "5"
                        || pgnMoveString[1].toString() == "6"
                        || pgnMoveString[1].toString() == "7"
                        || pgnMoveString[1].toString() == "8"
                        )
            ) {
                movingPiece = ChessPieceName.ROOK
                toSquare = stringToChessSquare(pgnMoveString.substring(2,4))
                movingPieceRow = stringToRow(pgnMoveString[1].toString())
            }

            // knight moves

            if ((pgnMoveString.length == 3)
                && (pgnMoveString[0].toString() =="N")) {
                movingPiece = ChessPieceName.KNIGHT
                toSquare = stringToChessSquare(pgnMoveString.substring(1,3))
            }

            if ((pgnMoveString.length == 4)
                && (pgnMoveString[0].toString() == "N")
                && (pgnMoveString[1].toString() == "a"
                        || pgnMoveString[1].toString() == "b"
                        || pgnMoveString[1].toString() == "c"
                        || pgnMoveString[1].toString() == "d"
                        || pgnMoveString[1].toString() == "e"
                        || pgnMoveString[1].toString() == "f"
                        || pgnMoveString[1].toString() == "g"
                        || pgnMoveString[1].toString() == "h"
                        )
            ) {
                movingPiece = ChessPieceName.KNIGHT
                toSquare = stringToChessSquare(pgnMoveString.substring(2,4))
                movingPieceCol = stringToCol(pgnMoveString[1].toString())
            }

            if ((pgnMoveString.length == 4)
                && (pgnMoveString[0].toString() == "N")
                && (pgnMoveString[1].toString() == "1"
                        || pgnMoveString[1].toString() == "2"
                        || pgnMoveString[1].toString() == "3"
                        || pgnMoveString[1].toString() == "4"
                        || pgnMoveString[1].toString() == "5"
                        || pgnMoveString[1].toString() == "6"
                        || pgnMoveString[1].toString() == "7"
                        || pgnMoveString[1].toString() == "8"
                        )
            ) {
                movingPiece = ChessPieceName.KNIGHT
                toSquare = stringToChessSquare(pgnMoveString.substring(2,4))
                movingPieceRow = stringToRow(pgnMoveString[1].toString())
            }




            Log.d(TAG,"movingpiece:$movingPiece")
            Log.d(TAG,"toSquare:$toSquare")
            Log.d(TAG,"col:$movingPieceCol")
            Log.d(TAG,"row:$movingPieceCol")

            for (move in legalMoves) {
                if (move.chessPiece.chessPieceName == movingPiece
                    && toSquare == move.to) {
                    if ((movingPieceRow == null) && (movingPieceCol == null)) {
                        movePieceWithoutValidation(move.from, toSquare)
                    }
                    if (movingPieceCol == move.from.col) {
                        movePieceWithoutValidation(move.from, toSquare)
                    }
                    if (movingPieceRow == move.from.row) {
                        movePieceWithoutValidation(move.from, toSquare)
                    }
                }
            }

        }
        validMoves.removeAll(validMoves)
        validMoves.addAll(getAllValidMoves())
        puzzleStartingPosition = toFen()
        puzzlePlayerToMove = playerToMove
        Log.d(TAG, "loadPuzzleStartingPosition: $puzzleStartingPosition")
    }
    
    fun loadPuzzlePositions() {
        if (puzzleStartingPosition.isEmpty() || solution.isEmpty()) return
        if (puzzlePlayerToMove == null) return
        puzzlePositions.removeAll(puzzlePositions)
        puzzlePositions.add(puzzleStartingPosition)
        var solutionMovesString : String = solution
        solutionMovesString = solutionMovesString.replace("[", "")
        solutionMovesString = solutionMovesString.replace("]", "")
        val solutionMoves = solutionMovesString.split(", ")
        Log.d(TAG, "loadPuzzlePositions: $solutionMovesString $solutionMoves")
        
        for (move in solutionMoves) {
            val from = stringToChessSquare(move.substring(0, 2))
            val to = stringToChessSquare(move.substring(2, 4))
            movePiece(from, to)
            puzzlePositions.add(toFen())
        }

        Log.d(TAG, "loadPuzzlePositions: $puzzlePositions")
        loadFEN(puzzleStartingPosition)
        playerToMove = puzzlePlayerToMove as ChessPlayer
        validMoves.removeAll(validMoves)
        validMoves.addAll(getAllValidMoves())
    } 

    fun setPuzzlePosition () {
        if (puzzlePositions.isEmpty()) return
        loadFEN(puzzlePositions[puzzlePositionIndex])
        playerToMove = puzzlePlayerToMove
        Log.d(TAG, "setPuzzlePosition: $playerToMove")
        validMoves.removeAll(validMoves)
        validMoves.addAll(getAllValidMoves())
    }

    fun increasePuzzleIndex () {
        val tempPuzzleIndex = puzzlePositionIndex + 1
        if (tempPuzzleIndex <= puzzlePositions.size - 1) puzzlePositionIndex = tempPuzzleIndex
    }

    fun decreasePuzzleIndex () {
        val tempPuzzleIndex = puzzlePositionIndex - 1
        if (tempPuzzleIndex > 0) puzzlePositionIndex = tempPuzzleIndex
    }
    
    
    
    

    fun setPuzzleData(response : Response<DailyPuzzleData>) {
                solution = response.body()?.puzzle?.solution.toString()
                puzzlePgn = response.body()?.game?.pgn.toString()
    }
    
    fun checkPuzzleLoaded () : Boolean {
        return  (puzzleStartingPosition.isNotEmpty())
    }

    fun stopGame () {
        validMoves.removeAll(validMoves)
        gameOn = false
    }

    fun startGame () {
        gameOn = true
    }

    fun setPuzzleActive () {
        isPuzzle = true
    }

    fun setPuzzleInactive () {
        isPuzzle = false
    }

    fun isPuzzleActive () : Boolean {
        return isPuzzle
    }

    fun isPuzzleCompleted () : Boolean {
        return (puzzlePositionIndex == puzzlePositions.size - 1)
    }

    fun isMoveCorrect () : Boolean {
        val currentFen = toFen()
        return (currentFen == puzzlePositions[puzzlePositionIndex])
    }

    fun hasPuzzleMoveMade () : Boolean {
        val currentFen = toFen()
        return if (puzzlePositionIndex == 0) {
            currentFen != puzzlePositions[puzzlePositionIndex]
        } else {
            (currentFen != puzzlePositions[puzzlePositionIndex - 1])
        }
    }

    fun getPuzzlePlayerToMove () : String {
        return if (puzzlePlayerToMove == ChessPlayer.WHITE) {
            "White to move!"
        } else {
            "Black to move!"
        }
    }

}