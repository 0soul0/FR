package com.testflight.flangear.query_algo

fun getStandard(queryId: Int): List<String> {
    val namesList = mutableListOf<String>()
    standardQueryWithId(queryId).forEach {
        namesList.add(it.name!!)
    }
    return namesList
}