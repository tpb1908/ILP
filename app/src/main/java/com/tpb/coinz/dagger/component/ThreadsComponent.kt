package com.tpb.coinz.dagger.component

import com.tpb.coinz.dagger.module.ChatModule
import com.tpb.coinz.dagger.module.ConnectivityModule
import com.tpb.coinz.dagger.module.UserModule
import com.tpb.coinz.view.messaging.threads.ThreadsActivity
import com.tpb.coinz.view.messaging.threads.ThreadsViewModel
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = [ConnectivityModule::class, ChatModule::class, UserModule::class])
interface ThreadsComponent {



    fun inject(viewModel: ThreadsViewModel)

}