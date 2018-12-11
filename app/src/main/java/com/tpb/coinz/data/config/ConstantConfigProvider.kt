package com.tpb.coinz.data.config

import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds

object ConstantConfigProvider : ConfigProvider {

    private val points = arrayListOf(LatLng(55.946233, -3.192473)
            , LatLng(55.946233, -3.184319)
            , LatLng(55.942617, -3.192473)
            , LatLng(55.942617, -3.184319)
    )

    override val collectionDistance: Int
        get() = 25
    override val coinsPerMap: Int
        get() = 50
    override val dailyBankLimit: Int
        get() = 25
    override val collectionAreaBounds: LatLngBounds
        get() = LatLngBounds.Builder().includes(points).build()
}