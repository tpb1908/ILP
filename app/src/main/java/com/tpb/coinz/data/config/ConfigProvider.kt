package com.tpb.coinz.data.config

import com.mapbox.mapboxsdk.geometry.LatLngBounds

interface ConfigProvider {

    val collectionDistance: Int

    val coinsPerMap: Int

    val dailyBankLimit: Int

    val collectionAreaBounds: LatLngBounds

}