package com.testflight.flangear.model

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class FlangeDetail(
    @Id
    var id: Long = 0,
    var queryId: Int = 0,
    var name: String? = null
)