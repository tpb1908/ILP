package com.tpb.coinz.dagger.module

import com.tpb.coinz.data.coins.CoinCollector
import com.tpb.coinz.data.location.LocationProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [LocationModule::class])
class CoinCollectionModule {

    @Singleton
    @Provides
    fun provideCoinCollector(locationProvider: LocationProvider): CoinCollector {
        return CoinCollector(locationProvider)
    }

}