package com.tpb.coinz.dagger.component

import androidx.appcompat.app.AppCompatActivity
import com.tpb.coinz.dagger.module.ConfigModule
import com.tpb.coinz.dagger.module.ConnectivityModule
import com.tpb.coinz.dagger.module.LocationModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ConfigModule::class, ConnectivityModule::class, LocationModule::class])
interface ActivityComponent {

    fun inject(activity: AppCompatActivity)

}