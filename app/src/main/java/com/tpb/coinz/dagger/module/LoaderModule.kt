package com.tpb.coinz.dagger.module

import com.tpb.coinz.data.coin.CoinLoader
import com.tpb.coinz.data.coin.download.CoinDownloader
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