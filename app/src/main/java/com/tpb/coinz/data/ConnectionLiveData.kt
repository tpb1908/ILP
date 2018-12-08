package com.tpb.coinz.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.lifecycle.LiveData

/**
 * [LiveData] which listens for [Intents][Intent] notifying of connectivity changes
 * Emits a boolean, whether the device is connected to a network
 * Registers and unregisters receiver with lifecycle changes
 */
class ConnectionLiveData(val context: Context) : LiveData<Boolean>() {

    @Suppress("DEPRECATION")
    override fun onActive() {
        super.onActive()
        // CONNECTIVITY_ACTION broadcasts will only be received in the foreground on API >= N
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        context.registerReceiver(receiver, filter)
    }

    override fun onInactive() {
        super.onInactive()
        context.unregisterReceiver(receiver)
    }

    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(c: Context, i: Intent) {
            if ("android.net.conn.CONNECTIVITY_CHANGE" == i.action) {
                val cm = c.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val network = cm.activeNetworkInfo
                postValue(network != null && network.isConnected)
            }
        }
    }
}
