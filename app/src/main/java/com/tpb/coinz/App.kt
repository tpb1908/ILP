package com.tpb.coinz

import android.app.Application
import com.mapbox.mapboxsdk.Mapbox
import com.tpb.coinz.dagger.component.DaggerHomeComponent
import com.tpb.coinz.dagger.component.DaggerMapComponent
import com.tpb.coinz.dagger.component.HomeComponent
import com.tpb.coinz.dagger.component.MapComponent
import com.google.firebase.FirebaseApp
import com.tpb.coinz.dagger.module.*


class App : Application() {

    lateinit var homeComponent: HomeComponent
    lateinit var mapComponent: MapComponent

    override fun onCreate() {
        super.onCreate()
        init()
    }

    private fun init() {
        val fbApp = FirebaseApp.initializeApp(this)!!
        Mapbox.getInstance(this, "pk.eyJ1IjoidHBiMTkwOCIsImEiOiJjam1vd25pZm0xNWQzM3ZvZWtpZ3hmdmQ5In0.YMMSu09MMG3QPZ4m6_zndQ")
        homeComponent = DaggerHomeComponent.builder()
                .loaderModule(LoaderModule())
                .locationModule(LocationModule(this))
                .storeModule(StoreModule(this))
                .backendModule(BackendModule())
                .build()
        mapComponent = DaggerMapComponent.builder()
                .loaderModule(LoaderModule())
                .locationModule(LocationModule(this))
                .storeModule(StoreModule(this))
                .connectivityModule(ConnectivityModule(this))
                .build()
    }

}