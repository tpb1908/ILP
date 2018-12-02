package com.tpb.coinz.view.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel<T> : ViewModel() {

    /**
     * [MutableLiveData] for emitting actions to the view
     *
     */
    abstract val actions: MutableLiveData<T>

    protected var firstBind = true
    open fun bind() {
        firstBind = false
    }

}