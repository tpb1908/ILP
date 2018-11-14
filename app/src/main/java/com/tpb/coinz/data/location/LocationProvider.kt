package com.tpb.coinz.data.location

import android.location.Location

interface LocationProvider {

    fun addListener(listener: LocationListener)

    fun removeListener(listener: LocationListener)

    fun start()

    fun pause()

    fun stop()

    fun lastLocationUpdate(): Location?
}