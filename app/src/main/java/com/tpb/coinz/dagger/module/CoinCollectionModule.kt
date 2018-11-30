package com.tpb.coinz.dagger.module

import com.google.firebase.firestore.FirebaseFirestore
import com.tpb.coinz.data.backend.CoinCollection
import com.tpb.coinz.data.backend.FireStoreCoinCollection
import com.tpb.coinz.data.coin.CoinCollector
import com.tpb.coinz.data.coin.CoinLoader
import com.tpb.coinz.data.coin.MapStore
import com.tpb.coinz.data.location.LocationProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [LocationModule::class, LoaderModule::class, StoreModule::class])
class CoinCollectionModule {

    @Singleton
    @Provides
    fun provideCoinCollector(locationProvider: LocationProvider, coinLoader: CoinLoader, mapStore: MapStore): CoinCollector {
        return CoinCollector(locationProvider, coinLoader, mapStore)
    }

    @Singleton
    @Provides
    fun provideCoinCollection(): CoinCollection {
        return FireStoreCoinCollection(FirebaseFirestore.getInstance())
    }

}