package com.tpb.coinz.dagger.component

import com.tpb.coinz.dagger.module.*
import com.tpb.coinz.messaging.threads.ThreadsActivity
import com.tpb.coinz.messaging.threads.ThreadsViewModel
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = [ConnectivityModule::class, ChatModule::class, UserModule::class])
interface ThreadsComponent {


    fun inject(activity: ThreadsActivity)

    fun inject(viewModel: ThreadsViewModel)

}