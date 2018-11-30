package com.tpb.coinz.dagger.component

import com.tpb.coinz.dagger.module.CoinCollectionModule
import com.tpb.coinz.dagger.module.UserModule
import com.tpb.coinz.view.home.HomeActivity
import com.tpb.coinz.view.home.HomeViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [UserModule::class, CoinCollectionModule::class])
interface HomeComponent {


    fun inject(activity: HomeActivity)


    fun inject(viewModel: HomeViewModel)

}