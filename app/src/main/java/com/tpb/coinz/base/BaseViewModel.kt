package com.tpb.coinz.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import java.lang.ref.WeakReference

abstract class BaseViewModel<N>(application: Application) : AndroidViewModel(application) {

    protected lateinit var navigator: WeakReference<N>

    public fun setNavigator(navigator: N) {
        this.navigator = WeakReference(navigator)
    }

    abstract fun init()

}