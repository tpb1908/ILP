package com.tpb.coinz.dagger.component

import com.tpb.coinz.view.bank.BankViewModel
import com.tpb.coinz.dagger.module.CoinCollectionModule
import com.tpb.coinz.dagger.module.ConnectivityModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ConnectivityModule::class, CoinCollectionModule::class])
interface BankComponent {

    fun inject(viewModel: BankViewModel)

}