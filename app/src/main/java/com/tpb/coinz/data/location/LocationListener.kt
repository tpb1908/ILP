package com.tpb.coinz.data.location

import android.location.Location

interface LocationListener {

    fun locationUpdate(location: Location)

    fun locationAvailable()

    fun locationUnavailable()

    fun locationUpdateError(exception: Exception)


}