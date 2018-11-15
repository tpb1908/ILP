package com.tpb.coinz.data.coins.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tpb.coinz.data.coins.Map

@Entity(tableName = "map")
data class RoomMap(@PrimaryKey(autoGenerate = true) var uid: Long = 0, val map: Map)