package com.tpb.coinz.dagger.module

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.tpb.coinz.data.coins.MapStore
import com.tpb.coinz.data.coins.room.RoomMapStore
import com.tpb.coinz.data.coins.room.SharedPrefsMapStore
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class StoreModule(val context: Context) {

    @Provides
    @Singleton
    fun provideMapStore(): MapStore {
        return SharedPrefsMapStore(context.getSharedPreferences("map_storage_prefs", Context.MODE_PRIVATE))
//        return RoomMapStore(
//                Room.databaseBuilder(context, RoomMapStore.Database::class.java, "db").addMigrations(object : Migration(1, 2) {
//                    override fun migrate(database: SupportSQLiteDatabase) {
//                        //Nothing to do
//                    }
//                }).build()
//        )
    }

}