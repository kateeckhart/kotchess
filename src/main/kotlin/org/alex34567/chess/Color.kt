package org.alex34567.chess

enum class Color(val nonPawnRank: Rank, val pawnRank: Rank) {
    WHITE(Rank.ONE, Rank.TWO),
    BLACK(Rank.EIGHT, Rank.SEVEN);

    operator fun not(): Color {
        return when (this) {
            WHITE -> BLACK
            BLACK -> WHITE
        }
    }
}

data class ColorTuple<T>(val black: T, val white: T) {
    fun of(color: Color): T {
        return when (color) {
            Color.BLACK -> black
            Color.WHITE -> white
        }
    }

    fun setColor(color: Color, thing: T): ColorTuple<T> {
        return when (color) {
            Color.BLACK -> copy(black = thing)
            Color.WHITE -> copy(white = thing)
        }
    }
}
