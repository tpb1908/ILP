package com.tpb.coinz.data.coins.room

import androidx.room.*
import com.tpb.coinz.data.coins.Map
import com.tpb.coinz.data.coins.MapStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class RoomMapStore(database: Database): MapStore {

    private val dao: MapDao = database.mapDao()

    @androidx.room.Database(entities = [RoomMap::class], version = 1)
    @TypeConverters(MapTypeConverter::class)
    public abstract class Database: RoomDatabase() {

        abstract fun mapDao(): MapDao

    }

    @Dao
    interface MapDao {

        @Query("SELECT * FROM map")
        fun getAllMaps(): List<RoomMap>

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insert(map: RoomMap): Long

    }

    override fun store(map: Map) {
        GlobalScope.launch(Dispatchers.IO) {
            dao.insert(RoomMap(map=map))
        }
    }

    override fun getLastStoreDate(callback: (Calendar) -> Unit) {
        getLatest {
            if (it != null) {
                val cal = Calendar.getInstance()
                cal.time = it.dateGenerated
                callback(cal)
            }
        }
    }

    override fun getLatest(callback: (Map?) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            val maps = dao.getAllMaps()
            callback(if (maps.isEmpty()) null else maps.last().map)
        }
    }
}