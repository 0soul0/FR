package com.testflight.flangear.query_algo

import kotlin.math.pow

fun getSubjectiveRadius(input: DomainSize, db: DomainSize, relativeError: DomainSize): Double {
    val square_x = (db.outerDia - input.outerDia).pow(2)
    val square_y = (db.boltDia - input.boltDia).pow(2)
    val square_z = (db.thickness - input.thickness).pow(2)

    val factor_x = 1 / ((relativeError.outerDia * 10000).roundToDecimal(0)).pow(2)
    val factor_y = 1 / ((relativeError.boltDia * 10000).roundToDecimal(0)).pow(2)
    val factor_z = 1 / ((relativeError.thickness * 10000).roundToDecimal(0)).pow(2)

    val radius = (factor_x * square_x + factor_y * square_y + factor_z * square_z).pow(0.5)
    return radius.roundToDecimal(7)
}