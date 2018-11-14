package com.tpb.coinz.data.coins.download

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.mapbox.mapboxsdk.geometry.LatLng
import com.tpb.coinz.data.coins.Coin
import com.tpb.coinz.data.coins.CoinLoader
import com.tpb.coinz.data.coins.Currency
import com.tpb.coinz.data.coins.Map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CoinDownloader : CoinLoader {

    private var listeners: ArrayList<(Map?) -> Unit> = arrayListOf()

    override fun loadCoins(date: Calendar, listener: (Map?) -> Unit) {
        listeners.add(listener)

        GlobalScope.launch(Dispatchers.IO) {
            loadCoinsFromUrl(date)
        }

    }

    private fun loadCoinsFromUrl(date: Calendar) {
        try {
            val datePath = convertToDatePath(date)
            val urlString = "http://homepages.inf.ed.ac.uk/stg/coinz/$datePath/coinzmap.geojson"
            val stream = downloadUrl(urlString)
            val json = JsonParser().parse(InputStreamReader(stream, "UTF-8")) as JsonObject
            val map = convert(json)
            listeners.forEach {it(map)}
            listeners.clear()
        } catch (ioe: IOException) {
            Log.e("CoinDownloader", "IOE $ioe")
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun convertToDatePath(cal: Calendar): String {
        return SimpleDateFormat("yyyy/MM/dd").format(cal.time)
    }


    // Given a string representation of a URL, sets up a connection and gets an input stream.
    @Throws(IOException::class)
    private fun downloadUrl(urlString: String): InputStream {
        val url = URL(urlString)
        val conn = url.openConnection() as HttpURLConnection
        // Also available: HttpsURLConnection
        with(conn) {
            readTimeout = 10000
            connectTimeout = 15000
            doInput = true
            requestMethod = "GET"
            connect()
        }
        return conn.inputStream
    }

    fun convert(obj: JsonObject): Map {

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