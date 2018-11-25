package com.tpb.coinz.dagger.component

import com.tpb.coinz.dagger.module.ChatModule
import com.tpb.coinz.dagger.module.ConnectivityModule
import com.tpb.coinz.dagger.module.UserModule
import com.tpb.coinz.messaging.ThreadViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ConnectivityModule::class, ChatModule::class, UserModule::class])
interface ThreadComponent {

    fun inject(viewModel: ThreadViewModel)

}