import com.testflight.flangear.model.FlangeV8
import com.testflight.flangear.query_algo.DomainSize
import com.testflight.flangear.query_algo.FlangeResultType
import com.testflight.flangear.query_algo.InputParameter
import com.testflight.flangear.query_algo.getDomainStatement
import com.testflight.flangear.query_algo.getStandard
import com.testflight.flangear.query_algo.getSubjectiveRadius

fun getResult(
    inputParameter: InputParameter,
    relativeError: DomainSize,
    threshold: DomainSize,
    errorMultiplier: Double
): List<FlangeResultType> {
    val queryResult = mutableListOf<FlangeResultType>()
    val unsortedResult = mutableListOf<RadiusIdPair>()

    val queryWithParameter: List<FlangeV8> = getDomainStatement(inputParameter = inputParameter, domainSize = threshold, errorMultiplier = errorMultiplier)

    queryWithParameter.forEach {
        val db_queryID = it.queryId
        val db_outterDia = it.outterDia
        val db_boltDia = it.boltCircleDia
        val db_thickness = it.thickness

        if (db_thickness < 0.0) return@forEach // skip if thickness is negative, negative means null

        val _dbParameter = DomainSize(outerDia = db_outterDia, boltDia = db_boltDia, thickness = db_thickness)
        val _input = DomainSize(outerDia = inputParameter.outerDia, boltDia = inputParameter.boltDia, thickness = inputParameter.thickness)
        val _subjectRadius: Double = getSubjectiveRadius(input = _input, db = _dbParameter, relativeError = relativeError)
        unsortedResult.add(RadiusIdPair(subjectiveRadius = _subjectRadius, queryID = db_queryID))
    }

    unsortedResult.sortedBy { it.subjectiveRadius }.forEach { radiusIdPair ->
        if (queryResult.find { it.queryID == radiusIdPair.queryID } == null) {
            val _name = getStandard(queryId = radiusIdPair.queryID)
            val _rank = queryResult.size + 1
            val tmp_result = FlangeResultType(rank = _rank, queryID = radiusIdPair.queryID, name = _name, errorMultiplier = errorMultiplier)
            queryResult.add(tmp_result)
        }
    }

    return queryResult
}

data class RadiusIdPair(
    val subjectiveRadius: Double,
    val queryID: Int
)