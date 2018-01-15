package org.alex34567.chess

enum class Color(val direction: Int, val firstRank: Rank) {
    WHITE(1, Rank.ONE),
    BLACK(-1, Rank.EIGHT);

    val secondRank = Rank.newInstance(firstRank.toInt() + direction)!!
    val thirdRank = Rank.newInstance(secondRank.toInt() + direction)!!
    val fourthRank = Rank.newInstance(thirdRank.toInt() + direction)!!
    val fifthRank = Rank.newInstance(fourthRank.toInt() + direction)!!
    val sixthRank = Rank.newInstance(fifthRank.toInt() + direction)!!
    val seventhRank = Rank.newInstance(sixthRank.toInt() + direction)!!
    val eighthRank = Rank.newInstance(seventhRank.toInt() + direction)!!

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
