package org.alex34567.chess

import java.util.*

enum class BoardSide(val file: File) {
    QUEEN_SIDE(File.A),
    KING_SIDE(File.H);

    companion object {
        fun fromFile(file: File): BoardSide? {
            return when (file) {
                File.A -> QUEEN_SIDE
                File.H -> KING_SIDE
                else -> null
            }
        }
    }

}

class CastleRights private constructor(private val rawRights: Set<BoardSide>) {

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
        val QUEEN_SIDE = CastleRights(EnumSet.of(BoardSide.QUEEN_SIDE))
        val KING_SIDE = CastleRights(EnumSet.of(BoardSide.KING_SIDE))
        val NONE = CastleRights(EnumSet.noneOf(BoardSide::class.java))
    }
}

data class State(val turn: Color, val board: Board, val castleRights: ColorTuple<CastleRights>,
                 val phantom: Pos?, val phantomRemove: Boolean, val resetDraw: Boolean) {
    val isCheck: ColorTuple<Boolean> get() = ColorTuple(false, false)

    fun movePiece(from: Pos, to: Pos, promote: Piece.Type? = null): State {
        val piece = board[from] ?: return this
        return piece.type.move(this, from, to, promote)
    }

    fun canCastle(side: BoardSide): Boolean {
        val rights = castleRights.of(turn)

        if (!rights.canCastle(side)) return false

        if (isCheck.of(turn)) return false

        val rookPos = Pos(turn.firstRank, side.file)
        if (board[rookPos]?.type != Piece.Type.ROOK) return false

        val kingPos = Pos(turn.firstRank, File.E)
        if (board[kingPos]?.type != Piece.Type.KING) return false

        val files = ArrayList<File>(4)
        files.addAll(when (side) {
            BoardSide.QUEEN_SIDE -> File.B..File.D
            BoardSide.KING_SIDE -> File.F..File.G
        })

        for (file in files) {
            if (board[turn.secondRank][file] != null) return false
        }

        for (file in files) {
            if (movePiece(kingPos, Pos(turn.firstRank, file)).isCheck.of(turn)) return false
        }

        return true
    }

    fun castle(side: BoardSide): State {
        if (!canCastle(side)) return this

        val currKingPos = Pos(turn.firstRank, File.E)

        val newKingPos = Pos(turn.firstRank, when (side) {
            BoardSide.QUEEN_SIDE -> File.C
            BoardSide.KING_SIDE -> File.G
        })

        val newRookPos = Pos(turn.firstRank, when (side) {
            BoardSide.QUEEN_SIDE -> File.D
            BoardSide.KING_SIDE -> File.F
        })

        val currRookPos = Pos(turn.firstRank, side.file)

        return movePiece(currKingPos, newKingPos).movePiece(currRookPos, newRookPos)
    }

    companion object {
        val DEFAULT = State(turn = Color.WHITE, board = Board.CHESSBOARD,
                castleRights = ColorTuple(CastleRights.ALL, CastleRights.ALL), phantomRemove = false, phantom = null,
                resetDraw = false)
    }
}
