package com.tpb.coinz.dagger.module

import com.google.firebase.firestore.FirebaseFirestore
import com.tpb.coinz.data.coin.CoinCollector
import com.tpb.coinz.data.coin.collection.CoinCollection
import com.tpb.coinz.data.coin.collection.FireStoreCoinCollection
import com.tpb.coinz.data.coin.loading.MapLoader
import com.tpb.coinz.data.coin.storage.MapStore
import com.tpb.coinz.data.config.ConfigProvider
import com.tpb.coinz.data.location.LocationProvider
import dagger.Module
import dagger.Provides
import timber.log.Timber
import javax.inject.Singleton

@Module(includes = [LocationModule::class, MapModule::class, ConfigModule::class])
class CoinCollectionModule {

    @Singleton
    @Provides
    fun provideCoinCollector(locationProvider: LocationProvider, mapLoader: MapLoader, mapStore: MapStore, config: ConfigProvider): CoinCollector {
        Timber.i("Providing coin collector")
        return CoinCollector(locationProvider, mapLoader, mapStore, config)
    }

    @Singleton
    @Provides
    fun provideCoinCollection(): CoinCollection {
        return FireStoreCoinCollection(FirebaseFirestore.getInstance())
    }


}