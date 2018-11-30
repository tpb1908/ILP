package com.tpb.coinz.dagger.module

import com.tpb.coinz.data.coins.CoinCollector
import com.tpb.coinz.data.coins.CoinLoader
import com.tpb.coinz.data.coins.MapStore
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

}