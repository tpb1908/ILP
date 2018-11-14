package com.tpb.coinz.data

import android.graphics.Color
import com.google.gson.JsonObject
import com.mapbox.mapboxsdk.geometry.LatLng
import com.tpb.coinz.data.coins.Coin
import com.tpb.coinz.data.coins.Currency
import com.tpb.coinz.data.coins.Map
import java.util.*

object Converter {

    public fun convert(obj: JsonObject): Map {

        val ratesObj = obj.getAsJsonObject("rates")
        val rates = ratesObj.entrySet().map {
            Currency.fromString(it.key) to it.value.asDouble
        }.toMap()

        val features = obj.getAsJsonArray("features")

        val coins = arrayListOf<Coin>()

        for (i in 0 until features.size()) {
            val feature = features.get(i).asJsonObject
            val properties = feature.getAsJsonObject("properties")
            val id = properties.getAsJsonPrimitive("id").asString
            val value = properties.getAsJsonPrimitive("value").asDouble
            val currency = Currency.fromString(properties.getAsJsonPrimitive("currency").asString)
            val markerSymbol = properties.getAsJsonPrimitive("marker-symbol").asInt
            val markerColor = Color.parseColor(properties.getAsJsonPrimitive("marker-color").asString)
            val coordinates = feature.getAsJsonObject("geometry").getAsJsonArray("coordinates")
            val long = coordinates.get(0).asDouble
            val lat = coordinates.get(1).asDouble
            coins.add(
                    Coin(id, value, currency, markerSymbol, markerColor, LatLng(lat, long)))
        }
        return Map(Date(), rates, coins)
    }

}