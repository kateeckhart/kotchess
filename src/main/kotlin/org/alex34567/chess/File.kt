package org.alex34567.chess

/** Represents a file in chess
 *
 */
class File private constructor(private val rawFile: Int) : Comparable<File> {

    override fun toString(): String {
        return ('A' + rawFile).toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        if (other !is File) return false

        if (rawFile == other.toInt()) return true

        return false
    }

    override fun hashCode(): Int {
        return rawFile.hashCode()
    }

    operator fun plus(other: File): File? {
        return newInstance(rawFile + other.toInt())
    }

    operator fun minus(other: File): File? {
        return newInstance(rawFile - other.toInt())
    }

    override operator fun compareTo(other: File): Int {
        if (rawFile < other.toInt()) return -1

        if (rawFile > other.toInt()) return 1

        return 0
    }

    operator fun rangeTo(other: File): Iterable<File> {
        //Handle backwards case
        if (this >= other) return emptyList()

        //Ignore null because other cannot be out of bounds
        return (rawFile..other.toInt()).map({ newInstance(it)!! })
    }

    fun toInt(): Int {
        return rawFile
    }

    companion object {

        private val POOL = genPool()

        val A get() = POOL[0]
        val B get() = POOL[1]
        val C get() = POOL[2]
        val D get() = POOL[3]
        val E get() = POOL[4]
        val F get() = POOL[5]
        val G get() = POOL[6]
        val H get() = POOL[7]

        private fun genPool(): List<File> {
            val pool: MutableList<File> = ArrayList(8)

            for (x in 0..7) {
                pool.add(File(x))
            }

            return pool
        }

        fun newInstance(rank: Int): File? {
            if (rank < 0 || rank > 7) return null

            return POOL[rank]
        }
    }
}
