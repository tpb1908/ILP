package com.tpb.coinz.dagger.module

import android.content.Context
import com.tpb.coinz.data.coin.loading.MapDownloader
import com.tpb.coinz.data.coin.loading.MapLoader
import com.tpb.coinz.data.coin.storage.MapStore
import com.tpb.coinz.data.coin.storage.SharedPrefsMapStore
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module class MapModule(val context: Context) {

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

    @Singleton
    @Provides
    fun provideMapLoader(): MapLoader {
        return MapDownloader()
    }

}