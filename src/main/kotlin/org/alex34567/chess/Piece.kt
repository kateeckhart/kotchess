package org.alex34567.chess

import java.util.Objects.hash

class Piece private constructor(val type: Type, val color: Color) {
    enum class Type(val whiteChar: Char, val blackChar: Char) {
        PAWN('♙', '♟') {
            override fun move(state: State, from: Pos, to: Pos, promote: Type?): State {
                val color = state.board[from]?.color ?: return state
                val newState = state.copy(resetDraw = true)

                if (to.rank == color.eighthRank) {
                    val promotePiece = Piece.newInstance(promote ?: QUEEN, color)
                    val newBoard = newState.board.replacePiece(from, promotePiece)
                    return super.move(state.copy(board = newBoard), from, to, promote)
                }

                if (from.rank == color.secondRank && to.rank == color.fourthRank) {
                    val enPassantPos = from.copy(rank = color.thirdRank)
                    val newBoard = newState.board.replacePiece(enPassantPos, newInstance(ENPASSANT, color))
                    return super.move(newState.copy(board = newBoard, phantom = enPassantPos, phantomRemove = false),
                            from, to, promote)
                }

                return super.move(newState, from, to, promote)
            }

            override fun canMove(state: State, from: Pos, to: Pos): Boolean {
                val color = state.board[from]?.color ?: return false

                if (from.rank == color.secondRank && to.rank == color.fourthRank) return super.canMove(state, from, to)

                if ((to.rank.toInt() - from.rank.toInt()) != color.direction) return false

                if (from.rank.absSub(to.rank) != 1) return false

                val fileDistance = from.file.absSub(to.file)

                return if (fileDistance == 0) {
                    super.canMove(state, from, to)
                } else {
                    if (fileDistance == 1) {
                        if (state.board[to] == null) return false

                        super.canMove(state, from, to)
                    } else {
                        false
                    }
                }

            }
        },
        QUEEN('♕', '♛') {
            override fun canMove(state: State, from: Pos, to: Pos): Boolean {
                return ROOK.canMove(state, from, to) || BISHOP.canMove(state, from, to)
            }
        },
        KING('♔', '♚') {
            override fun move(state: State, from: Pos, to: Pos, promote: Type?): State {
                val color = state.board[to]?.color ?: return state

                val newState = state.copy(castleRights = state.castleRights.setColor(color, CastleRights.NONE))
                return super.move(newState, from, to, promote)
            }

            override fun canMove(state: State, from: Pos, to: Pos): Boolean {
                if (from == to) return false

                val rankDistance = from.rank.absSub(to.rank)
                val fileDistance = from.file.absSub(to.file)

                if (rankDistance > 1 || fileDistance > 1) return false

                return super.canMove(state, from, to)
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

            override fun canMove(state: State, from: Pos, to: Pos): Boolean {
                val rankDistance = from.rank.absSub(to.rank)
                val fileDistance = from.file.absSub(to.file)

                fun <T : RankFile<T>> collideDetect(from: T, to: T, indexer: (T) -> Piece?): Boolean {
                    var rookRankFile: T? = from
                    val direction = if (to > from) 1 else -1

                    while (rookRankFile != to) {
                        if (rookRankFile == null) return false

                        if (indexer(rookRankFile) != null) {
                            return false
                        }

                        rookRankFile = rookRankFile.factory.newInstance(rookRankFile.toInt() + direction)
                    }
                    return true
                }

                if (rankDistance != 0 && fileDistance == 0) {
                    return if (collideDetect(from.rank, to.rank, { state.board[it][from.file] })) {
                        super.canMove(state, from, to)
                    } else {
                        false
                    }
                }

                if (rankDistance == 0 && fileDistance != 0) {
                    return if (collideDetect(from.file, to.file, { state.board[from.rank][it] })) {
                        super.canMove(state, from, to)
                    } else {
                        false
                    }
                }

                return false
            }
        },
        BISHOP('♗', '♝') {
            override fun canMove(state: State, from: Pos, to: Pos): Boolean {
                val rankDistance = from.rank.absSub(to.rank)
                val fileDistance = from.file.absSub(to.file)

                if (rankDistance != fileDistance) return false

                val rankDirection = if (to.rank > from.rank) 1 else -1
                val fileDirection = if (to.rank > from.rank) 1 else -1

                var bishopPos = from

                while (bishopPos != to) {
                    if (state.board[bishopPos] != null) {
                        return false
                    }

                    val rankSum = Rank.newInstance(bishopPos.rank.toInt() + rankDirection)
                    val fileSum = File.newInstance(bishopPos.file.toInt() + fileDirection)

                    if (rankSum == null || fileSum == null) return false

                    bishopPos = Pos(rankSum, fileSum)
                }

                return super.canMove(state, from, to)
            }
        },
        KNIGHT('♘', '♞') {
            override fun canMove(state: State, from: Pos, to: Pos): Boolean {

                val rankDistance = from.rank.absSub(to.rank)
                val fileDistance = from.file.absSub(to.file)
                if ((rankDistance == 2 && fileDistance == 1) || (rankDistance == 1 && fileDistance == 2)) {
                    return super.canMove(state, from, to)
                }

                return false
            }
        },
        ENPASSANT('█', '█') {
            override fun onCapture(state: State, pos: Pos): State {
                val color = state.board[pos]?.color ?: return state

                val pawnPos = pos.copy(rank = color.fourthRank)
                val newBoard = state.board.replacePiece(pawnPos, null)

                return super.onCapture(state.copy(board = newBoard), pos)
            }

            override fun canMove(state: State, from: Pos, to: Pos): Boolean = false
        };

        open fun move(state: State, from: Pos, to: Pos, promote: Type?): State {
            val phantomPiece = if (state.phantom == null) null else state.board[state.phantom]
            var newState = state
            if (state.phantomRemove && state.phantom != null && phantomPiece != null) {
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
            newState = newState.copy(turn = !newState.turn, board = newBoard, phantomRemove = true)
            return newState
        }

        open fun canMove(state: State, from: Pos, to: Pos): Boolean {
            if (state.movePiece(from, to).isCheck.of(state.turn)) return false

            return true
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
