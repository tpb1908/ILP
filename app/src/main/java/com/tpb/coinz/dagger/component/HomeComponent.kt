package com.tpb.coinz.dagger.component

import com.tpb.coinz.dagger.module.LoaderModule
import com.tpb.coinz.dagger.module.LocationModule
import com.tpb.coinz.home.HomeActivity
import com.tpb.coinz.home.HomeViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules= arrayOf(LoaderModule::class, LocationModule::class))
interface HomeComponent {


    fun inject(activity: HomeActivity)


    fun inject(viewModel: HomeViewModel)

}