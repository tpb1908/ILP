package com.tpb.coinz.data.coins.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.tpb.coinz.data.coins.Map

class MapTypeConverter {

    @TypeConverter
    fun mapToString(map: Map): String {
        return Gson().toJson(map)
    }

    @TypeConverter
    fun stringToMap(json: String): Map {
        return Gson().fromJson(json, Map::class.java)
    }


}