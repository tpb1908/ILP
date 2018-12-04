package com.tpb.coinz.dagger.component

import android.app.Service
import com.tpb.coinz.dagger.module.CoinCollectionModule
import com.tpb.coinz.dagger.module.UserModule
import dagger.Component

@Component(modules = [UserModule::class, CoinCollectionModule::class])
interface ServiceComponent {

    fun inject(service: Service)

}