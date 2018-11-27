package com.tpb.coinz.dagger.component

import com.tpb.coinz.dagger.module.*
import com.tpb.coinz.messaging.ThreadsActivity
import com.tpb.coinz.messaging.ThreadsViewModel
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = [ConnectivityModule::class, ChatModule::class, UserModule::class])
interface MessagesComponent {


    fun inject(activity: ThreadsActivity)

    fun inject(viewModel: ThreadsViewModel)

}