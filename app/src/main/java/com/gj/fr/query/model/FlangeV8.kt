package com.testflight.flangear.model

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
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

@Entity
data class FlangeV8(
    @Id
    var id: Long = 0,
    var flangeId: Int = 0,
    var queryId: Int = 0,
    var name: String? = null,
    var innerDia: Double = 0.0,
    var outterDia: Double = 0.0,
    var boltCircleDia: Double = 0.0,
    var thickness: Double = 0.0,
    var material: String? = null,
    var numOfBolts: Int = 0,
    var holeSize: Double = 0.0,
    var bolt: String? = null,
    var flangeType: String? = null,
    var flangeClass: String? = null
)
