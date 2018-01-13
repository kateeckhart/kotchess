package org.alex34567.chess

import java.util.Objects.hash

class Piece private constructor(val type: Type, val color: Color) {
    enum class Type(val whiteChar: Char, val blackChar: Char) {
        PAWN('♙', '♟'),
        QUEEN('♕', '♛'),
        KING('♔', '♚'),
        ROOK('♖', '♜'),
        BISHOP('♗', '♝'),
        KNIGHT('♘', '♞');
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        if (other !is Piece) return false

        if (type == other.type && color == other.color) return true

        return false
    }

    override fun hashCode(): Int {
        return hash(type, color)
    }

    override fun toString(): String {
        return when (color) {
            Color.BLACK -> type.blackChar
            Color.WHITE -> type.whiteChar
        }.toString()
    }

    companion object Factory {

        fun newInstance(type: Type, color: Color): Piece {
            return Piece(type, color)
        }
    }
}
