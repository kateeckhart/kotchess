package org.alex34567.chess

/** A helper class for the position of the piece.
 *
 * @param rank the rank of the position
 * @param file the file of the position
 */
data class Pos(val rank: Rank, val file: File)

private fun nonPawnRow(color: Color): Row {
    return Row.makeRow(color, Piece.Type.ROOK, Piece.Type.KNIGHT, Piece.Type.BISHOP, Piece.Type.QUEEN,
            Piece.Type.KING, Piece.Type.BISHOP, Piece.Type.KNIGHT, Piece.Type.ROOK)
}


/**
 * A immutable board for chess or chess-like games.
 */
class Board private constructor(private val rawBoard: List<Row>) : Iterable<Row> {

    companion object {
        /** A board a chess game at startup.
         *
         */
        val CHESSBOARD = genBoard()

        private fun genBoard(): Board {
            val board = ArrayList<Row>(8)
            //Generate rank 1
            board.add(nonPawnRow(Color.WHITE))

            //Generate rank 2
            board.add(Row.makeRow(Piece.newInstance(Piece.Type.PAWN, Color.WHITE)))

            //Generate ranks 3-6
            for (x in 3..6) {
                board.add(Row.EMPTY)
            }

            //Generate rank 7
            board.add(Row.makeRow(Piece.newInstance(Piece.Type.PAWN, Color.BLACK)))

            //Generate rank 8
            board.add(nonPawnRow(Color.BLACK))

            return Board(board)
        }
    }

    private operator fun List<Row>.get(rank: Rank): Row {
        return this[rank.toInt() - 1]
    }

    private operator fun MutableList<Row>.set(rank: Rank, row: Row) {
        this[rank.toInt() - 1] = row
    }

    /** Indexes the board by rank.
     *
     * @param rank the rank of the row
     * @return the row at rank
     */
    operator fun get(rank: Rank): Row {
        return rawBoard[rank]
    }

    /** Indexes the board by pos.
     *
     * @param pos the possition of the piece
     * @return the piece at pos
     */
    operator fun get(pos: Pos): Piece? {
        return this[pos.rank][pos.file]
    }

    /** Moves a piece.
     *
     * @param from the position of the piece you want to move
     * @param to where the piece will go
     * @return a board with the piece moved
     */
    fun movePiece(from: Pos, to: Pos): Board {
        val newBoard = ArrayList(rawBoard)
        val fromNewRow = Row.Builder(this[from.rank])
        val piece = fromNewRow[from.file]
        fromNewRow[from.file] = null
        newBoard[from.rank] = fromNewRow.build()

        val toNewRow = Row.Builder(newBoard[to.rank])
        toNewRow[to.file] = piece
        newBoard[to.rank] = toNewRow.build()
        return Board(newBoard)
    }

    /** Iterates though the rows.
     *
     * @return An iterator though the rows
     */
    override fun iterator(): Iterator<Row> {
        return rawBoard.iterator()
    }

    /** Generates a multi-line string representation.
     *
     * @return A string representation
     */
    override fun toString(): String {
        val strBuilder = StringBuilder()
        for (row in reversed()) {
            strBuilder.appendln(row)
        }
        return strBuilder.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        if (other !is Board) return false

        for ((thisRow, otherRow) in this.zip(other)) {
            if (thisRow != otherRow) return false
        }

        return true
    }

    override fun hashCode(): Int {
        return rawBoard.hashCode()
    }

}
