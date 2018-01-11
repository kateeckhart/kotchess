@file:JvmName("Main")

package org.alex34567.chess

fun main(args: Array<String>) {
    var board = Board.CHESSBOARD
    board = board.movePiece(Pos(Rank.newInstance(2)!!, File.A), Pos(Rank.newInstance(4)!!, File.A))
            .movePiece(Pos(Rank.newInstance(4)!!, File.A), Pos(Rank.newInstance(4)!!, File.C))
            .movePiece(Pos(Rank.newInstance(1)!!, File.A), Pos(Rank.newInstance(7)!!, File.A))

    println(board)
    print(Board.CHESSBOARD)
}