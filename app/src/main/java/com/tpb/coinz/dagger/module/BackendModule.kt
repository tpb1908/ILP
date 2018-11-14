package com.tpb.coinz.dagger.module

import com.google.firebase.firestore.FirebaseFirestore
import com.tpb.coinz.data.backend.CoinCollection
import com.tpb.coinz.data.backend.FireStoreCoinCollection
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class BackendModule {

    @Singleton
    @Provides
    fun provideCoinCollection(): CoinCollection {
        return FireStoreCoinCollection(FirebaseFirestore.getInstance())
    }

}