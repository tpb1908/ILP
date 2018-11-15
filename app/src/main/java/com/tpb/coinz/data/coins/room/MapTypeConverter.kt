package com.tpb.coinz.data.coins.room

import android.util.Log
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.tpb.coinz.data.coins.Map

class MapTypeConverter {

    @TypeConverter
    fun mapToString(map: Map): String {
        val json = Gson().toJson(map)
        Log.i("MapTypeConverter", "Map JSON is $json ")
        return json
    }

    @TypeConverter
    fun stringToMap(json: String): Map {
        val map = Gson().fromJson(json, Map::class.java)
        Log.i("MapTypeConverter", "Map from JSON is $map")
        return map
    }


}