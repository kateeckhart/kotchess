package org.alex34567.chess

sealed class DrawDetector() {

    abstract fun addMove(move: State): DrawDetector

    abstract val isDraw: Boolean

    companion object {
        val EMPTY: DrawDetector = DrawNotDetected(HashMap<State, Int>(0), 0)
    }
}

private class DrawNotDetected(val repetition: Map<State, Int>, val nonCap: Int) : DrawDetector() {

    override fun addMove(move: State): DrawDetector {
        val map = repetition.toMutableMap()
        val count = map.compute(move, {_: State, count: Int? ->
            count?.plus(1) ?: 1
        })
        if (nonCap == 99 || count == 3) {
            return DrawDetected
        }
        return DrawNotDetected(map, nonCap + 1)
    }

    override val isDraw: Boolean get() = false
}


private object DrawDetected : DrawDetector() {
    override fun addMove(move: State): DrawDetector {return this}

    override val isDraw: Boolean get() = true
}
