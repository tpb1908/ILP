package com.tpb.coinz.dagger.component

import com.tpb.coinz.bank.BankViewModel
import com.tpb.coinz.dagger.module.BackendModule
import com.tpb.coinz.dagger.module.ConnectivityModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ConnectivityModule::class, BackendModule::class])
interface BankComponent {

    fun inject(viewModel: BankViewModel)

}