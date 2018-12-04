package com.tpb.coinz.dagger.module

import com.tpb.coinz.data.config.ConfigProvider
import com.tpb.coinz.data.config.ConstantConfigProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ConfigModule {

    @Provides
    @Singleton
    fun provideConfigProvider(): ConfigProvider {
        return ConstantConfigProvider()
    }

}