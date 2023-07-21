package com.testflight.flangear.model.json_db_model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

//{"name":"data_v8","seq":1412}

data class SequenceJson(
    @Expose
    @SerializedName("name")
    val name: String,

    @Expose
    @SerializedName("seq")
    val seq: Int,
)