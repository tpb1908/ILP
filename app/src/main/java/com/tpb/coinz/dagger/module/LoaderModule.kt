package com.tpb.coinz.dagger.module

import com.tpb.coinz.data.coin.loading.MapLoader
import com.tpb.coinz.data.coin.loading.MapDownloader
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class LoaderModule {

    @Singleton
    @Provides
    fun provideCoinLoader(): MapLoader {
        return MapDownloader()
    }

}