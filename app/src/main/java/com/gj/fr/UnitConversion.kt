package com.gj.fr

import android.icu.text.DecimalFormat
import com.google.ar.sceneform.math.Vector3

object UnitConversion {

    fun vectorToCm(float:Float):Float{
        val df = DecimalFormat("#.##")
        return df.format(float).toFloat()
    }

    fun cmToVector3(x:Float,y:Float,z:Float):Vector3{
        return Vector3(x,y,z)
    }

    fun cmToVector(float:Float):Float{
        val df = DecimalFormat("#.##")
        return df.format(float/400).toFloat()
    }

}