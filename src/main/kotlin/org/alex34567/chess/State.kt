package org.alex34567.chess

import java.util.*

enum class BoardSide {
    QueenSide,
    KingSide,
}

class CastleRights private constructor(private val rawRights: Set<BoardSide>) {

    fun kingMoved(): CastleRights {
        return CastleRights(EnumSet.noneOf(BoardSide::class.java))
    }

    fun rookMoved(side: BoardSide): CastleRights {
        val set = EnumSet.copyOf(rawRights)
        set.remove(side)
        return CastleRights(set)
    }

    fun canCastle(side: BoardSide): Boolean {
        return rawRights.contains(side)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        if (other !is CastleRights) return false

        return rawRights == other.rawRights
    }

    override fun hashCode(): Int {
        return rawRights.hashCode()
    }

    companion object {
        val ALL = CastleRights(EnumSet.allOf(BoardSide::class.java))
        val QUEEN_SIDE = CastleRights(EnumSet.of(BoardSide.QueenSide))
        val KING_SIDE = CastleRights(EnumSet.of(BoardSide.KingSide))
    }
}

data class State(val turn: Color, val board: Board, val castle: CastleRights)