package com.testflight.flangear.query_algo

import com.testflight.flangear.model.FlangeV8
//import com.testflight.flangear.model.FlangeV8_
import com.testflight.flangear.utilities.ObjectBox
import io.objectbox.kotlin.query
import kotlin.math.abs

data class InputParameter(
    val outerDia: Double,
    val boltDia: Double,
    val thickness: Double,
    val numOfBolts: Int
)

data class DomainSize(
    val outerDia: Double = 0.0,
    val boltDia: Double = 0.0,
    val thickness: Double = 0.0
)

fun getDomainStatement(
    inputParameter: InputParameter,
    domainSize: DomainSize,
    errorMultiplier: Double
): List<FlangeV8> {
    return listOf()
}
//    var lowerBound = DomainSize()
//    var upperBound = DomainSize()
//
//    if ((domainSize.outerDia * errorMultiplier) >= 1 || (domainSize.boltDia * errorMultiplier) >= 1  || (domainSize.thickness * errorMultiplier) >= 1) {
//        if ((domainSize.outerDia * errorMultiplier) >= 1) {
//            if ((domainSize.boltDia * errorMultiplier) >= 1) {
//                if ((domainSize.thickness * errorMultiplier) >= 1) {
//                    lowerBound = DomainSize(outerDia= abs(0.99 + 1), boltDia= abs(0.99 + 1), thickness= abs(0.99 + 1))
//                    upperBound = DomainSize(outerDia= abs(0.99 - 1), boltDia= abs(0.99 - 1), thickness= abs(0.99 - 1))
//                }
//                else {
//                    lowerBound = DomainSize(outerDia= abs(0.99 + 1), boltDia= abs(0.99 + 1), thickness= abs(domainSize.thickness * errorMultiplier + 1))
//                    upperBound = DomainSize(outerDia= abs(0.99 - 1), boltDia= abs(0.99 - 1), thickness= abs(domainSize.thickness * errorMultiplier - 1))
//                }
//            }
//            else {
//                lowerBound = DomainSize(outerDia= abs(0.99 + 1), boltDia= abs(domainSize.boltDia * errorMultiplier + 1), thickness= abs(domainSize.thickness * errorMultiplier + 1))
//                upperBound = DomainSize(outerDia= abs(0.99 - 1), boltDia= abs(domainSize.boltDia * errorMultiplier - 1), thickness= abs(domainSize.thickness * errorMultiplier - 1))
//            }
//        }
//    } else {
//        lowerBound = DomainSize(outerDia= abs(domainSize.outerDia * errorMultiplier + 1), boltDia= abs(domainSize.boltDia * errorMultiplier + 1), thickness= abs(domainSize.thickness * errorMultiplier + 1))
//        upperBound = DomainSize(outerDia= abs(domainSize.outerDia * errorMultiplier - 1), boltDia= abs(domainSize.boltDia * errorMultiplier - 1), thickness= abs(domainSize.thickness * errorMultiplier - 1))
//    }
//
//    val dataV8Box = ObjectBox.store.boxFor(FlangeV8::class.java)
//    val dataQuery = dataV8Box.query {
//        between(FlangeV8_.outterDia, (inputParameter.outerDia / lowerBound.outerDia), (inputParameter.outerDia / upperBound.outerDia))
//        between(FlangeV8_.boltCircleDia, (inputParameter.boltDia / lowerBound.boltDia), (inputParameter.boltDia / upperBound.boltDia))
//        between(FlangeV8_.thickness, (inputParameter.thickness / lowerBound.thickness), (inputParameter.thickness / upperBound.thickness))
//        equal(FlangeV8_.numOfBolts, (inputParameter.numOfBolts).toLong())
//    }
//    val queryResult = dataQuery.find()
//    dataQuery.close()
//    return queryResult
//}