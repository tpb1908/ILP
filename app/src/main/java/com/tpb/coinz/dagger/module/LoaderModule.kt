package com.tpb.coinz.dagger.module

import com.tpb.coinz.data.coins.CoinLoader
import com.tpb.coinz.data.coins.download.CoinDownloader
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class LoaderModule {

    @Singleton
    @Provides
    fun provideCoinLoader(): CoinLoader {
        return CoinDownloader()
    }

}