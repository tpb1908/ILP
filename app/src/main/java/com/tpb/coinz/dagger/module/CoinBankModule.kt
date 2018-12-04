package com.tpb.coinz.dagger.module

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.tpb.coinz.data.coin.bank.CoinBank
import com.tpb.coinz.data.coin.bank.FireStoreCoinBank
import com.tpb.coinz.data.config.ConfigProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [ConfigModule::class])
class CoinBankModule(val context: Context) {

    @Singleton
    @Provides
    fun provideCoinBank(config: ConfigProvider): CoinBank {
        return FireStoreCoinBank(context.getSharedPreferences("coinbank", Context.MODE_PRIVATE), FirebaseFirestore.getInstance(), config)
    }
}