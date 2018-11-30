package com.tpb.coinz.dagger.component

import com.tpb.coinz.dagger.module.*
import com.tpb.coinz.view.map.MapActivity
import com.tpb.coinz.view.map.MapViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [LocationModule::class, ConnectivityModule::class, UserModule::class, CoinCollectionModule::class])
interface MapComponent {


    fun inject(activity: MapActivity)

    fun inject(viewModel: MapViewModel)

}