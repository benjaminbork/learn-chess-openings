package com.example.learnchessopenings

class ChessModel {
    override fun toString(): String {
        var description = ""
        var rank = 8
        for (row in 0..7) {
            description += "$rank"
            rank -= 1
            for(col in 0..7) {
                description += " ."
            }
            description += "\n"
        }
        description += "  A B C D E F G H"
        return description
    }
}