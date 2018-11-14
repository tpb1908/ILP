package com.tpb.coinz.dagger.module

import android.app.Application
import android.content.res.Resources
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module class AppModule(val application: Application) {

    @Provides
    @Singleton
    fun provideApplication(): Application = application

    @Provides
    @Singleton
    fun provideResources(application: Application): Resources = application.resources

}