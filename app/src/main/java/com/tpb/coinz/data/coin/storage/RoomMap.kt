package com.tpb.coinz.data.coin.storage

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tpb.coinz.data.coin.Map

@Entity(tableName = "map")
data class RoomMap(val map: Map, @PrimaryKey var uid: Long = map.dateGenerated.timeInMillis)