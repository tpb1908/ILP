package com.tpb.coinz.base

import androidx.lifecycle.ViewModel
import java.lang.ref.WeakReference

abstract class BaseViewModel<N> : ViewModel() {

    protected lateinit var navigator: WeakReference<N>

    public fun setNavigator(navigator: N) {
        this.navigator = WeakReference(navigator)
    }

    abstract fun init()

}