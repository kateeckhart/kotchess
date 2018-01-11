package org.alex34567.chess

private fun genRow(): MutableList<Piece?> {
    val row: MutableList<Piece?> = ArrayList(8)
    for (x in 0..7) {
        row.add(null)
    }
    return row
}

class Row private constructor(private val rawRow: List<Piece?>) : Iterable<Piece?> {

    operator fun get(file: File): Piece? {
        return rawRow[file.toInt()]
    }

    override fun iterator(): Iterator<Piece?> {
        return rawRow.iterator()
    }

    override fun toString(): String {
        val strBuilder = StringBuilder()
        for (piece in this) {
            strBuilder.append(piece?.toString() ?: '█')
        }
        return strBuilder.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        if (other !is Row) return false

        for ((thisPiece, otherPiece) in this.zip(other)) {
            if (thisPiece != otherPiece) return false
        }

        return true
    }

    override fun hashCode(): Int {
        return rawRow.hashCode()
    }

    class Builder private constructor(private val rawRow: MutableList<Piece?>) : Iterable<Piece?> {
        constructor() : this(genRow())

        constructor(row: Row) : this({
            val newRow: MutableList<Piece?> = ArrayList(8)
            newRow.addAll(row)
            newRow
        }())

        operator fun get(file: File): Piece? {
            return rawRow[file.toInt()]
        }

        override fun iterator(): Iterator<Piece?> {
            return rawRow.iterator()
        }

        fun build(): Row {
            return Row(ArrayList(rawRow))
        }

        operator fun set(file: File, piece: Piece?) {
            rawRow[file.toInt()] = piece
        }
    }

    companion object {

        val EMPTY = Row(genRow())

        fun makeRow(piece: Piece): Row {
            val row = Row.Builder()

            for (file in File.A..File.H) {
                row[file] = piece
            }
            return row.build()
        }

        fun makeRow(
                p0: Piece?, p1: Piece?, p2: Piece?, p3: Piece?, p4: Piece?, p5: Piece?, p6: Piece?, p7: Piece?): Row {
            val row = Row.Builder()
            row[File.A] = p0
            row[File.B] = p1
            row[File.C] = p2
            row[File.D] = p3
            row[File.E] = p4
            row[File.F] = p5
            row[File.G] = p6
            row[File.H] = p7
            return row.build()
        }

        fun makeRow(color: Color, p0: Piece.Type?, p1: Piece.Type?, p2: Piece.Type?, p3: Piece.Type?,
                    p4: Piece.Type?, p5: Piece.Type?, p6: Piece.Type?, p7: Piece.Type?): Row {
            val pieceGen = { type: Piece.Type? -> if (type == null) null else Piece.newInstance(type, color) }

            return makeRow(pieceGen(p0), pieceGen(p1), pieceGen(p2), pieceGen(p3),
                    pieceGen(p4), pieceGen(p5), pieceGen(p6), pieceGen(p7))
        }
    }

}


