package com.tpb.coinz.dagger.module

import com.google.firebase.firestore.FirebaseFirestore
import com.tpb.coinz.data.backend.ChatCollection
import com.tpb.coinz.data.backend.FireStoreChatCollection
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ChatModule {

    @Singleton
    @Provides
    fun provideChatCollection(): ChatCollection {
        return FireStoreChatCollection(FirebaseFirestore.getInstance())
    }

}