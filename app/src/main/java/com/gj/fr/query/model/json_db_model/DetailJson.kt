package com.testflight.flangear.model.json_db_model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

//{"query_ID":1,"Name":"DN 15 | 14/16 | AS 4087"},

data class DetailJson(
    @Expose
    @SerializedName("query_ID")
    val queryId: Int,

    @Expose
    @SerializedName("Name")
    val name: String,
)
