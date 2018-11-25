package com.tpb.coinz.messaging

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tpb.coinz.base.BaseViewModel
import com.tpb.coinz.data.backend.ChatCollection
import javax.inject.Inject

class ThreadViewModel : BaseViewModel<ThreadViewModel.ThreadAction>() {

    @Inject
    lateinit var chatCollection: ChatCollection

    override val actions = MutableLiveData<ThreadAction>()

    override fun bind() {

    }

    sealed class ThreadAction {}
}