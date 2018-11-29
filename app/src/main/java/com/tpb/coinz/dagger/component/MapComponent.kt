package com.tpb.coinz.dagger.component

import com.tpb.coinz.dagger.module.*
import com.tpb.coinz.map.MapActivity
import com.tpb.coinz.map.MapViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [LoaderModule::class, LocationModule::class, StoreModule::class, ConnectivityModule::class, BackendModule::class, UserModule::class, CoinCollectionModule::class])
interface MapComponent {


    fun inject(activity: MapActivity)

    fun inject(viewModel: MapViewModel)

}