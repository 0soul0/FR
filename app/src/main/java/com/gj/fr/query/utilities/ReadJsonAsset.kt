package com.testflight.flangear.utilities

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.testflight.flangear.model.FlangeDetail
import com.testflight.flangear.model.FlangeSequence
import com.testflight.flangear.model.FlangeV8
import com.testflight.flangear.model.json_db_model.DetailJson
import com.testflight.flangear.model.json_db_model.SequenceJson
import com.testflight.flangear.model.json_db_model.V8Json
import timber.log.Timber
import java.lang.NumberFormatException
import java.lang.reflect.Type

fun Context.readJsonAsset(fileName: String): String {
    return assets.open(fileName).bufferedReader().use { it.readText() }
}

fun initWithJsonDB(context: Context) {
    val flangeDetailBox = ObjectBox.store.boxFor(FlangeDetail::class.java)
    val flangeSequenceBox = ObjectBox.store.boxFor(FlangeSequence::class.java)
    val flangeV8Box = ObjectBox.store.boxFor(FlangeV8::class.java)

    if (flangeDetailBox.count() == 0L) {
        val flangeDetailJsonString = context.readJsonAsset(DB_DETAILS_JSON)
        val flangeDetailList: List<DetailJson> = Gson().fromJson(flangeDetailJsonString, object : TypeToken<List<DetailJson>>() {}.type)

        ObjectBox.store.callInTxAsync({
            flangeDetailList.forEach {
                flangeDetailBox.put(FlangeDetail(queryId = it.queryId, name = it.name))
            }
            Timber.d("[initJsonDB]detailBox.count(): ${flangeDetailBox.count()}")
        }, null)
    }

    if (flangeSequenceBox.count() == 0L) {
        val flangeSequenceJsonString = context.readJsonAsset(DB_SEQUENCE_JSON)
        val flangeSequenceList: List<SequenceJson> = Gson().fromJson(flangeSequenceJsonString, object : TypeToken<List<SequenceJson>>() {}.type)

        ObjectBox.store.callInTxAsync({
            flangeSequenceList.forEach {
                flangeSequenceBox.put(FlangeSequence(name = it.name, seq = it.seq))
            }
            Timber.d("[initJsonDB]sequenceBox.count(): ${flangeSequenceBox.count()}")
        }, null)
    }

    if (flangeV8Box.count() == 0L) {
        val flangeV8JsonString = context.readJsonAsset(DB_V8_JSON)
        val gson = GsonBuilder().registerTypeAdapter(Double::class.java, object: JsonDeserializer<Double> {
            override fun deserialize(
                json: JsonElement?,
                typeOfT: Type?,
                context: JsonDeserializationContext?
            ): Double {
                return try {
                    json?.asDouble ?: -1.0
                } catch (e: NumberFormatException) {
                    -1.0
                }
            }
        }).create()
        val flangeV8List: List<V8Json> = gson.fromJson(flangeV8JsonString, object : TypeToken<List<V8Json>>() {}.type)

        ObjectBox.store.callInTxAsync({
            flangeV8List.forEach {
                flangeV8Box.put(FlangeV8(
                    flangeId = it.flangeId,
                    queryId = it.queryId,
                    name = it.name,
                    innerDia = it.innerDia,
                    outterDia = it.outterDia,
                    boltCircleDia = it.boltCircleDia,
                    thickness = it.thickness,
                    material = it.material,
                    numOfBolts = it.numOfBolts,
                    holeSize = it.holeSize,
                    bolt = it.bolt,
                    flangeType = it.flangeType,
                    flangeClass = it.flangeClass,
                ))
            }
            Timber.d("[initJsonDB]v8Box.count(): ${flangeV8Box.count()}")
        }, null)
    }
}