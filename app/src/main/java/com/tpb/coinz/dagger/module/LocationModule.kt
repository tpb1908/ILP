package com.tpb.coinz.dagger.module

import android.content.Context
import com.tpb.coinz.data.location.GMSLocationProvider
import com.tpb.coinz.data.location.LocationProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class LocationModule(val context: Context) {

    @Singleton
    @Provides
    fun provideLocationProvider(): LocationProvider = GMSLocationProvider(context)

}