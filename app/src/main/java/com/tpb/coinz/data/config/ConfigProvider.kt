package com.tpb.coinz.data.config

import com.mapbox.mapboxsdk.annotations.PolygonOptions
import com.mapbox.mapboxsdk.geometry.LatLngBounds

interface ConfigProvider {

    val collectionDistance: Int

    val maxCoinsIntMap: Int

    val maxDailyCoins: Int

    val collectionAreaBounds: LatLngBounds

    val collectionAreaPolygon: PolygonOptions

}