package com.tpb.coinz.dagger.module

import com.tpb.coinz.data.CoinDownloader
import com.tpb.coinz.data.CoinLoader
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