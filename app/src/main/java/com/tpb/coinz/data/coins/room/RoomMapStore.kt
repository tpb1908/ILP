package com.tpb.coinz.data.coins.room

import androidx.room.*
import com.tpb.coinz.Result
import com.tpb.coinz.data.coins.Map
import com.tpb.coinz.data.coins.MapStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class RoomMapStore(database: Database): MapStore {

    private val dao: MapDao = database.mapDao()

    @androidx.room.Database(entities = [RoomMap::class], version = 2)
    @TypeConverters(MapTypeConverter::class)
    abstract class Database: RoomDatabase() {

        abstract fun mapDao(): MapDao

    }

    @Dao
    interface MapDao {

        @Query("SELECT * FROM map")
        fun getAllMaps(): List<RoomMap>

        @Query("SELECT * FROM map ORDER BY uid DESC LIMIT 1")
        fun getMostRecent(): RoomMap?

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insert(map: RoomMap): Long

        @Update()
        fun update(map: RoomMap)

    }

    override fun store(map: Map) {
        GlobalScope.launch(Dispatchers.IO) {
            dao.insert(RoomMap(map=map))
        }
    }

    override fun update(map: Map) {
        GlobalScope.launch(Dispatchers.IO) {
            dao.update(RoomMap(map))
        }
    }

    override fun getLastStoreDate(callback: (Calendar) -> Unit) {
        getLatest {
            if (it is Result.Value<Map>) {
                callback(it.v.dateGenerated)
            }
        }
    }

    override fun getLatest(callback: (Result<Map>) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            val map = dao.getMostRecent()
            if (map == null) {
                callback(Result.None)
            } else {
                Timber.i("Loaded map $map")
                map.apply { callback(Result.Value(this.map)) }
            }

        }
    }
}