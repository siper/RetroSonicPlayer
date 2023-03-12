package ru.stersh.apisonic.models

enum class Rating(val value: Int) {
    REMOVE_RATING(0),
    RATE_1_STAR(1),
    RATE_2_STAR(2),
    RATE_3_STAR(3),
    RATE_4_STAR(4),
    RATE_5_STAR(5)
}