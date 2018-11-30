package com.tpb.coinz.data.location

import android.location.Location

interface LocationListener {

    fun locationUpdate(location: Location)

    fun locationAvailable()

    fun locationUnavailable()

    fun locationUpdateError(exception: Exception)

    interface SimpleLocationListener : LocationListener {

        override fun locationAvailable() {

        }

        override fun locationUnavailable() {
        }

        override fun locationUpdateError(exception: Exception) {
        }
    }

}