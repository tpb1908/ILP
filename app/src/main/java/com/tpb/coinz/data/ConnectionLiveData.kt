package com.tpb.coinz.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.lifecycle.LiveData


class ConnectionLiveData(val context: Context) : LiveData<Boolean>() {

    override fun onActive() {
        super.onActive()
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        context.registerReceiver(receiver, filter)
    }

    override fun onInactive() {
        super.onInactive()
        context.unregisterReceiver(receiver)
    }

    private val receiver = object: BroadcastReceiver() {

        override fun onReceive(c: Context, i: Intent) {
            if ("android.net.conn.CONNECTIVITY_CHANGE" == i.action) {
                val cm = c.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val network = cm.activeNetworkInfo
                postValue(network != null && network.isConnected)
            }
        }
    }
}
