package com.tpb.coinz.view.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel<T> : ViewModel() {

    /**
     * [MutableLiveData] for emitting actions to the view
     *
     */
    abstract val actions: MutableLiveData<T>


    abstract fun bind()

}