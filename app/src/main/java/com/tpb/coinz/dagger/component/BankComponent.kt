package com.tpb.coinz.dagger.component

import com.tpb.coinz.view.bank.BankViewModel
import com.tpb.coinz.dagger.module.CoinCollectionModule
import com.tpb.coinz.dagger.module.ConnectivityModule
import com.tpb.coinz.dagger.module.UserModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ConnectivityModule::class, CoinCollectionModule::class, UserModule::class])
interface BankComponent {

    fun inject(viewModel: BankViewModel)

}