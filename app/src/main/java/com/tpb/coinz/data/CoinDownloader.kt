package com.tpb.coinz.data

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.util.Log
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class CoinDownloader(val listener: (Map?) -> Unit) : AsyncTask<Calendar, Void, Map>() {

    override fun doInBackground(vararg cal: Calendar): Map? {
        try {
            val datePath = convertToDatePath(cal[0])
            val urlString = "http://homepages.inf.ed.ac.uk/stg/coinz/$datePath/coinzmap.geojson"
            val stream = downloadUrl(urlString)
            val json = JsonParser().parse(InputStreamReader(stream, "UTF-8")) as JsonObject
            return Converter.convert(json)
        } catch (ioe: IOException) {
            Log.e("CoinDownloader", "IOE $ioe")
        }
        return null
    }

    @SuppressLint("SimpleDateFormat")
    private fun convertToDatePath(cal: Calendar): String {
        return SimpleDateFormat("yyyy/MM/dd").format(cal.time)
    }

    override fun onPostExecute(result: Map?) {
        super.onPostExecute(result)

        listener(result)
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
//        conn.readTimeout = 10000 // milliseconds
//        conn.connectTimeout = 15000 // milliseconds
//        conn.requestMethod = "GET"
//        conn.doInput = true
//        conn.connect() // Starts the query
        return conn.inputStream
    }
}