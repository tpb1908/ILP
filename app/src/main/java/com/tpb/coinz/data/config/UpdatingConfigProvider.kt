package com.tpb.coinz.data.config

import android.content.SharedPreferences
import com.mapbox.mapboxsdk.annotations.PolygonOptions
import com.mapbox.mapboxsdk.geometry.LatLngBounds

class UpdatingConfigProvider(val prefs: SharedPreferences) : ConfigProvider {

    override val collectionDistance: Int
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val coinsPerMap: Int
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val dailyCollectionLimit: Int
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val collectionAreaBounds: LatLngBounds
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val collectionAreaPolygon: PolygonOptions
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
}