package org.alex34567.chess

enum class Color {
    WHITE,
    BLACK;

    operator fun not(): Color {
        return when (this) {
            WHITE -> BLACK
            BLACK -> WHITE
        }
    }
}
