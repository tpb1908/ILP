package com.tpb.coinz.data.config

import com.mapbox.mapboxsdk.annotations.PolygonOptions
import com.mapbox.mapboxsdk.geometry.LatLngBounds

interface ConfigProvider {

    val collectionDistance: Int

    val coinsPerMap: Int

    val dailyCollectionLimit: Int

    val collectionAreaBounds: LatLngBounds

    val collectionAreaPolygon: PolygonOptions

}