package com.tpb.coinz.dagger.module

import com.google.firebase.firestore.FirebaseFirestore
import com.tpb.coinz.data.backend.FireBaseUserCollection
import com.tpb.coinz.data.backend.UserCollection
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class UserModule {

    @Provides
    @Singleton
    fun provideUserCollection(): UserCollection {
        return FireBaseUserCollection(FirebaseFirestore.getInstance())
    }

}