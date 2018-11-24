package com.tpb.coinz.messaging

import android.app.Application
import com.tpb.coinz.base.BaseViewModel
import com.tpb.coinz.data.backend.ChatCollection
import javax.inject.Inject

class ThreadViewModel(application: Application) : BaseViewModel<ThreadNavigator>(application) {

    @Inject
    lateinit var chatCollection: ChatCollection

    override fun init() {

    }
}