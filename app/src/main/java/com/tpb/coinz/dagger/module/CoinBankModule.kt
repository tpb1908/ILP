package com.tpb.coinz.dagger.module

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.tpb.coinz.data.coin.bank.CoinBank
import com.tpb.coinz.data.coin.bank.FireStoreCoinBank
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module class CoinBankModule(val context: Context) {

    @Singleton
    @Provides
    fun provideCoinBank(): CoinBank {
        return FireStoreCoinBank(context.getSharedPreferences("coinbank", Context.MODE_PRIVATE), FirebaseFirestore.getInstance())
    }
}