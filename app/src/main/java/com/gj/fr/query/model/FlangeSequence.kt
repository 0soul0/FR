package com.testflight.flangear.model

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

//{"name":"data_v8","seq":1412}

@Entity
data class FlangeSequence(
    @Id
    var id: Long = 0,
    var name: String? = null,
    var seq: Int = 0
)
