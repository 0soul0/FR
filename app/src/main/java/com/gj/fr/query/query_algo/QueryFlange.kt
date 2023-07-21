package com.testflight.flangear.query_algo

import getResult


fun query(inputParameter: InputParameter): List<FlangeResultType>? {
    var errorMultiplier = 1.0
    val experimentError = DomainSize(outerDia = 0.009, boltDia = 0.0131, thickness = 0.044)
    var result = getResult(
        inputParameter = inputParameter,
        relativeError = experimentError,
        threshold = DomainSize(outerDia = 0.03, boltDia = 0.05, thickness = 0.2),
        errorMultiplier = errorMultiplier
    )

    while (result.isEmpty()) {
        errorMultiplier += 0.1
        result = getResult(
            inputParameter = inputParameter,
            relativeError = experimentError,
            threshold = DomainSize(outerDia = 0.03, boltDia = 0.05, thickness = 0.2),
            errorMultiplier = errorMultiplier
        )
        if (errorMultiplier > 100) {
//            Timber.d("[QueryFlange]errorMultiplier exceed 10, fail to find results")
            return null
        }
    }

    return result
}