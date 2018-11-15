package com.tpb.coinz.dagger.module

import android.content.Context
import com.tpb.coinz.data.ConnectionLiveData
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ConnectivityModule(val context: Context) {

    @Provides
    @Singleton
    fun provideConnectionLiveData(): ConnectionLiveData {
        return ConnectionLiveData(context)
    }

}