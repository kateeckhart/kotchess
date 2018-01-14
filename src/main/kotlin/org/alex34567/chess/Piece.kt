package org.alex34567.chess

import java.util.Objects.hash

class Piece private constructor(val type: Type, val color: Color) {
    enum class Type(val whiteChar: Char, val blackChar: Char) {
        PAWN('♙', '♟') {
            override fun move(state: State, from: Pos, to: Pos, promote: Type?): State {
                val color = state.board[from]?.color ?: return state
                val newState = state.copy(resetDraw = true)

                val promoteRank = (!color).nonPawnRank
                if (to.rank == promoteRank) {
                    val promotePiece = Piece.newInstance(promote ?: QUEEN, color)
                    val newBoard = newState.board.replacePiece(from, promotePiece)
                    return super.move(state.copy(board = newBoard), from, to, promote)
                }

                val secondRank = (color.pawnRank + Rank.ONE)!!
                val thirdRank = (secondRank + Rank.ONE)!!
                if (from.rank == color.pawnRank && to.rank == thirdRank) {
                    val enPassantPos = from.copy(rank = secondRank)
                    val newBoard = newState.board.replacePiece(enPassantPos, newInstance(ENPASSANT, color))
                    return super.move(newState.copy(board = newBoard, phantom = enPassantPos), from, to, promote)
                }

                return super.move(newState, from, to, promote)
            }
        },
        QUEEN('♕', '♛'),
        KING('♔', '♚') {
            override fun move(state: State, from: Pos, to: Pos, promote: Type?): State {
                val color = state.board[to]?.color ?: return state

                val newState = state.copy(castleRights = state.castleRights.setColor(color, CastleRights.NONE))
                return super.move(newState, from, to, promote)
            }
        },
        ROOK('♖', '♜') {
            override fun move(state: State, from: Pos, to: Pos, promote: Type?): State {
                val color = state.board[to]?.color ?: return state

                val rookSide = BoardSide.fromFile(from.file) ?: return super.move(state, from, to, promote)
                val newState = state.copy(castleRights =
                state.castleRights.setColor(color, state.castleRights.of(color).rookMoved(rookSide)))
                return super.move(newState, from, to, promote)
            }
        },
        BISHOP('♗', '♝'),
        KNIGHT('♘', '♞'),
        ENPASSANT('█', '█') {
            override fun onCapture(state: State, pos: Pos): State {
                val color = state.board[pos]?.color ?: return state

                val thirdRank = when (color) {
                    Color.BLACK -> Rank.FIVE
                    Color.WHITE -> Rank.FOUR
                }

                val pawnPos = pos.copy(rank = thirdRank)
                val newBoard = state.board.replacePiece(pawnPos, null)

                return super.onCapture(state.copy(board = newBoard), pos)
            }
        };

        open fun move(state: State, from: Pos, to: Pos, promote: Type?): State {
            val phantomPiece = if (state.phantom == null) null else state.board[state.phantom]
            var newState = state
            if (state.phantom != null && phantomPiece != null) {
                newState = phantomPiece.type.onPhantomVanish(newState, state.phantom)
                val newBoard = state.board.replacePiece(state.phantom, null)
                newState = newState.copy(board = newBoard)
            }

            val capturedPiece = newState.board[to]
            if (capturedPiece != null) {
                newState = capturedPiece.type.onCapture(newState, to)
            }

            val piece = newState.board[from]
            val newBoard = newState.board.replacePiece(from, null).replacePiece(to, piece)
            newState = newState.copy(turn = !newState.turn, board = newBoard)
            return newState
        }

        open fun onCapture(state: State, pos: Pos): State {
            return state.copy(resetDraw = true)
        }

        open fun onPhantomVanish(state: State, pos: Pos): State {
            return state.copy(phantom = null)
        }
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
