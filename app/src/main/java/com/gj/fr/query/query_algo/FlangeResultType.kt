package com.testflight.flangear.query_algo

import java.util.UUID

data class FlangeResultType(
    val id: UUID = UUID.randomUUID(),
    val rank: Int,
    val queryID: Int,
    val name: List<String>,
    val errorMultiplier: Double
)
