package org.alex34567.chess

class Rank private constructor(private val rawRank: Int) : Comparable<Rank> {

    override fun toString(): String {
        return Integer.toString(rawRank)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        if (other !is Rank) return false

        if (rawRank == other.toInt()) return true

        return false
    }

    override fun hashCode(): Int {
        return rawRank.hashCode()
    }

    operator fun plus(other: Rank): Rank? {
        return newInstance(rawRank + other.toInt())
    }

    operator fun minus(other: Rank): Rank? {
        return newInstance(rawRank - other.toInt())
    }

    override operator fun compareTo(other: Rank): Int {
        if (rawRank < other.toInt()) return -1

        if (rawRank > other.toInt()) return 1

        return 0
    }

    operator fun rangeTo(other: Rank): Iterable<Rank> {
        //Handle backwards case
        if (this >= other) return emptyList()

        //Ignore null because other cannot be out of bounds
        return (rawRank..other.toInt()).map({ newInstance(it)!! })
    }

    fun toInt(): Int {
        return rawRank
    }

    companion object {

        private val POOL = genPool()

        val ONE get() = POOL[0]
        val TWO get() = POOL[1]
        val THREE get() = POOL[2]
        val FOUR get() = POOL[3]
        val FIVE get() = POOL[4]
        val SIX get() = POOL[5]
        val SEVEN get() = POOL[6]
        val EIGHT get() = POOL[7]

        private fun genPool(): List<Rank> {
            val pool: MutableList<Rank> = ArrayList(8)

            for (x in 1..8) {
                pool.add(Rank(x))
            }

            return pool
        }

        fun newInstance(rank: Int): Rank? {
            if (rank < 1 || rank > 8) return null

            return POOL[rank - 1]
        }
    }
}
