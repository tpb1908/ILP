package com.tpb.coinz

import android.app.Application
import android.util.Log
import com.mapbox.mapboxsdk.Mapbox
import com.tpb.coinz.dagger.component.DaggerHomeComponent
import com.tpb.coinz.dagger.component.DaggerMapComponent
import com.tpb.coinz.dagger.component.HomeComponent
import com.tpb.coinz.dagger.component.MapComponent
import com.tpb.coinz.dagger.module.LoaderModule
import com.tpb.coinz.dagger.module.LocationModule

class App : Application() {

    lateinit var homeComponent: HomeComponent
    lateinit var mapComponent: MapComponent

    override fun onCreate() {
        super.onCreate()
        init()
    }

    private fun init() {
        Log.i("App", "onCreate init called")
        Mapbox.getInstance(this, "pk.eyJ1IjoidHBiMTkwOCIsImEiOiJjam1vd25pZm0xNWQzM3ZvZWtpZ3hmdmQ5In0.YMMSu09MMG3QPZ4m6_zndQ")
        LocationListener.init(applicationContext)
        homeComponent = DaggerHomeComponent.builder()
                .loaderModule(LoaderModule())
                .locationModule(LocationModule(this))
                .build()
        mapComponent = DaggerMapComponent.builder()
                .loaderModule(LoaderModule())
                .locationModule(LocationModule(this))
                .build()
    }

}