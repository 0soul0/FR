package com.testflight.flangear.query_algo

import com.testflight.flangear.model.FlangeDetail
import com.testflight.flangear.model.FlangeDetail_
import com.testflight.flangear.utilities.ObjectBox
import io.objectbox.kotlin.query

fun standardQueryWithId(queryId: Int): List<FlangeDetail> {
    val dataDetailBox = ObjectBox.store.boxFor(FlangeDetail::class.java)
    val dataDetailQuery = dataDetailBox.query {
        equal(FlangeDetail_.queryId, queryId.toLong())
    }
    val queryDetailResult = dataDetailQuery.find()
    dataDetailQuery.close()
    return queryDetailResult
}