package com.tpb.coinz.view.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel<T> : ViewModel() {


    abstract val actions: MutableLiveData<T>


    abstract fun bind()

}