package com.testflight.flangear.query_algo

import kotlin.math.pow
import kotlin.math.round

fun Double.roundToDecimal(fractionDigits: Int): Double {
    val multiplier = 10.0.pow(fractionDigits)
    return round(this * multiplier) / multiplier
}