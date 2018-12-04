package com.tpb.coinz.data.coin.loading

import android.annotation.SuppressLint
import android.graphics.Color
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.mapbox.mapboxsdk.geometry.LatLng
import com.tpb.coinz.data.coin.Coin
import com.tpb.coinz.data.coin.Currency
import com.tpb.coinz.data.coin.Map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import javax.net.ssl.HttpsURLConnection

/**
 * Implementation of [MapLoader] which downloads the map JSON in a coroutine and parses it
 * Largely the same code as provided in the course slides
 */
class MapDownloader : MapLoader {

    private var listeners: ArrayList<(Result<Map>) -> Unit> = arrayListOf()

    override fun loadCoins(date: Calendar, listener: (Result<Map>) -> Unit) {
        listeners.add(listener)
        GlobalScope.launch(Dispatchers.IO) { // launch download on the IO threadpool
            loadCoinsFromUrl(date)
        }

    }

    private fun loadCoinsFromUrl(date: Calendar) {
        try {
            val datePath = convertToDatePath(date)
            val urlString = "https://homepages.inf.ed.ac.uk/stg/coinz/$datePath/coinzmap.geojson"
            val stream = downloadUrl(urlString)
            val json = JsonParser().parse(InputStreamReader(stream, "UTF-8")) as JsonObject
            val map = convert(json)
            listeners.forEach { it(Result.success(map)) }
            listeners.clear()
        } catch (ioe: IOException) {
            Timber.e(ioe, "MapDownloader exception")
            listeners.forEach { it(Result.failure(ioe)) }
            listeners.clear()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun convertToDatePath(cal: Calendar): String {
        return SimpleDateFormat("yyyy/MM/dd").format(cal.time)
    }


    @Throws(IOException::class)
    private fun downloadUrl(urlString: String): InputStream {
        val url = URL(urlString)
        val conn = url.openConnection() as HttpsURLConnection
        with(conn) {
            readTimeout = 10000
            connectTimeout = 15000
            doInput = true
            requestMethod = "GET"
            connect()
        }
        return conn.inputStream
    }

    private fun convert(obj: JsonObject): Map {

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
        return Map(Calendar.getInstance(), rates, coins)
    }
}