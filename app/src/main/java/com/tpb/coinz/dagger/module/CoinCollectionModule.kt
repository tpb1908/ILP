package com.tpb.coinz.dagger.module

import com.google.firebase.firestore.FirebaseFirestore
import com.tpb.coinz.data.coin.collection.CoinCollection
import com.tpb.coinz.data.coin.collection.FireStoreCoinCollection
import com.tpb.coinz.data.coin.CoinCollector
import com.tpb.coinz.data.coin.loading.MapLoader
import com.tpb.coinz.data.coin.storage.MapStore
import com.tpb.coinz.data.location.LocationProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [LocationModule::class, LoaderModule::class, StoreModule::class])
class CoinCollectionModule {

    @Singleton
    @Provides
    fun provideCoinCollector(locationProvider: LocationProvider, mapLoader: MapLoader, mapStore: MapStore): CoinCollector {
        return CoinCollector(locationProvider, mapLoader, mapStore)
    }

    @Singleton
    @Provides
    fun provideCoinCollection(): CoinCollection {
        return FireStoreCoinCollection(FirebaseFirestore.getInstance())
    }

}