package com.tpb.coinz.view.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel<T> : ViewModel() {

    /**
     * [ActionLiveData] for emitting actions to the view
     *
     */
    abstract val actions: ActionLiveData<T>
    //TODO: The SetLoadingState 'actions' should probably not be actions as we want loading state to be preserved on config change

    val loadingState = MutableLiveData<Boolean>()

    protected var firstBind = true
    open fun bind() {
        firstBind = false
    }


}