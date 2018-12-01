package com.tpb.coinz.data.coin.storage

import android.content.SharedPreferences
import com.google.gson.Gson
import com.tpb.coinz.Result
import com.tpb.coinz.data.coin.Map
import java.util.*

/**
 * Simple implementation of [MapStore] which stores a single [Map]
 */
class SharedPrefsMapStore(private val prefs: SharedPreferences) : MapStore {

    private val key = "map"
    private var latest: Map? = null // cache of Map

    override fun store(map: Map) {
        prefs.edit().putString(key, Gson().toJson(map)).apply()
        latest = map
    }

    override fun update(map: Map) {
        prefs.edit().putString(key, Gson().toJson(map)).apply()
        latest = map
    }


    override fun getLatest(callback: (Result<Map>) -> Unit) {
        if (latest == null) {
            if (prefs.contains(key)) {
                val json = prefs.getString(key, null)
                if (json != null) {
                    callback(Result.Value(Gson().fromJson(json, Map::class.java)))
                }
            } else {
                callback(Result.None)
            }
        } else {
            // Return from cached value if available
            callback(Result.Value(latest!!))
        }
    }
}