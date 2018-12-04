package com.tpb.coinz.dagger.component

import com.tpb.coinz.dagger.module.CoinBankModule
import com.tpb.coinz.view.bank.BankViewModel
import com.tpb.coinz.dagger.module.CoinCollectionModule
import com.tpb.coinz.dagger.module.ConnectivityModule
import com.tpb.coinz.dagger.module.UserModule
import com.tpb.coinz.view.bank.BankActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [CoinCollectionModule::class, UserModule::class, CoinBankModule::class])
interface BankComponent {

    fun inject(viewModel: BankViewModel)


}