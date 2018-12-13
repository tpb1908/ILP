package com.tpb.coinz.view.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel<T> : ViewModel() {

    /**
     * [ActionLiveData] for emitting actions to the view
     */
    abstract val actions: ActionLiveData<T>

    /**
     * [MutableLiveData] boolean for whether or not the [ViewModel] is in a loading state
     */
    val loadingState = MutableLiveData<Boolean>()

    private var firstBind = true
    open fun bind() {
        firstBind = false
    }


}