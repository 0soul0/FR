package com.testflight.flangear.model.json_db_model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

//{
//    "ID":1,
//    "query_ID":1,
//    "Name":"DN15 | A | AS 2129:1994",
//    "inner_dia":15,
//    "outter_dia":95,
//    "bolt_circle_dia":67,
//    "thickness":13,
//    "material":"Iron",
//    "num_of_bolts":4,
//    "Hole_size":14,
//    "Bolt":"M12",
//    "Flange_Type":"AS 2129:1994",
//    "Class":"A"
//}

data class V8Json(
    @Expose
    @SerializedName("ID")
    val flangeId: Int,

    @Expose
    @SerializedName("query_ID")
    val queryId: Int,

    @Expose
    @SerializedName("Name")
    val name: String,

    @Expose
    @SerializedName("inner_dia")
    val innerDia: Double,

    @Expose
    @SerializedName("outter_dia")
    val outterDia: Double,

    @Expose
    @SerializedName("bolt_circle_dia")
    val boltCircleDia: Double,

    @Expose
    @SerializedName("thickness")
    val thickness: Double,

    @Expose
    @SerializedName("material")
    val material: String,

    @Expose
    @SerializedName("num_of_bolts")
    val numOfBolts: Int,

    @Expose
    @SerializedName("Hole_size")
    val holeSize: Double,

    @Expose
    @SerializedName("Bolt")
    val bolt: String,

    @Expose
    @SerializedName("Flange_Type")
    val flangeType: String,

    @Expose
    @SerializedName("Class")
    val flangeClass: String,
)
