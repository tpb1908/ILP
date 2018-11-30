package com.tpb.coinz.data.coin.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.tpb.coinz.data.coin.Map

class MapTypeConverter {

    @TypeConverter
    fun mapToString(map: Map): String = Gson().toJson(map)

    @TypeConverter
    fun stringToMap(json: String): Map = Gson().fromJson(json, Map::class.java)


}