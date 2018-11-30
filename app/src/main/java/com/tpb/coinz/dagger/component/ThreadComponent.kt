package com.tpb.coinz.dagger.component

import com.tpb.coinz.dagger.module.*
import com.tpb.coinz.view.messaging.thread.ThreadViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ConnectivityModule::class, ChatModule::class, UserModule::class, CoinCollectionModule::class])
interface ThreadComponent {

    fun inject(viewModel: ThreadViewModel)

}