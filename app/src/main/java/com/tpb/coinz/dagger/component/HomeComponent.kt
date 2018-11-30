package com.tpb.coinz.dagger.component

import com.tpb.coinz.dagger.module.*
import com.tpb.coinz.home.HomeActivity
import com.tpb.coinz.home.HomeViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules= [LoaderModule::class, LocationModule::class, BackendModule::class, StoreModule::class, CoinCollectionModule::class])
interface HomeComponent {


    fun inject(activity: HomeActivity)


    fun inject(viewModel: HomeViewModel)

}