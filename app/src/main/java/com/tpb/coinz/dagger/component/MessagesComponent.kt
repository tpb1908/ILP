package com.tpb.coinz.dagger.component

import com.tpb.coinz.dagger.module.*
import com.tpb.coinz.messaging.MessagesActivity
import com.tpb.coinz.messaging.MessagesViewModel
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = [ConnectivityModule::class, ChatModule::class, UserModule::class])
interface MessagesComponent {


    fun inject(activity: MessagesActivity)

    fun inject(viewModel: MessagesViewModel)

}