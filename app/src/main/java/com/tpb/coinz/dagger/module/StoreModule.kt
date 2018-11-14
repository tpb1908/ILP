package com.tpb.coinz.dagger.module

import android.content.Context
import androidx.room.Room
import com.tpb.coinz.data.coins.MapStore
import com.tpb.coinz.data.coins.room.RoomMapStore
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class StoreModule(val context: Context) {

    @Provides
    @Singleton
    fun provideMapStore(): MapStore {
        return RoomMapStore(Room.databaseBuilder(context, RoomMapStore.Database::class.java, "db").build())
    }

}