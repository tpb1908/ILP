package com.tpb.coinz.dagger.component

import com.tpb.coinz.dagger.module.ConfigModule
import com.tpb.coinz.dagger.module.ConnectivityModule
import com.tpb.coinz.dagger.module.LocationModule
import com.tpb.coinz.view.bank.BankActivity
import com.tpb.coinz.view.home.HomeActivity
import com.tpb.coinz.view.map.MapActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ConfigModule::class, ConnectivityModule::class, LocationModule::class])
interface ActivityComponent {

    fun inject(activity: HomeActivity)

    fun inject(activity: MapActivity)

    fun inject(activity: BankActivity)

}