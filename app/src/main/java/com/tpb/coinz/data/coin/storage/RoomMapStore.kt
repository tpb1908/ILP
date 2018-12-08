package com.tpb.coinz.data.coin.storage

import androidx.room.*
import com.tpb.coinz.data.coin.Map
import com.tpb.coinz.data.util.CoinzException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

class RoomMapStore(database: Database) : MapStore {

    private val dao: MapDao = database.mapDao()

    //TODO: Rewrite the store
    @androidx.room.Database(entities = [RoomMap::class], version = 2)
    @TypeConverters(MapTypeConverter::class)
    abstract class Database : RoomDatabase() {

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
            dao.insert(RoomMap(map = map))
        }
    }

    override fun update(map: Map) {
        GlobalScope.launch(Dispatchers.IO) {
            dao.update(RoomMap(map))
            getLatest {
                if (it.isSuccess) {
                    Timber.i("CoinCollectorImpl Latest map ${it.getOrThrow().remainingCoins.size}")
                }
            }
        }
    }

    override fun getLatest(callback: (Result<Map>) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            val map = dao.getMostRecent()
            if (map == null) {
                callback(Result.failure(CoinzException.NotFoundException()))
            } else {
                Timber.i("Loaded map $map")
                map.apply { callback(Result.success(this.map)) }
            }

        }
    }
}