package com.tpb.coinz

import android.app.Application
import com.mapbox.mapboxsdk.Mapbox
import com.google.firebase.FirebaseApp
import com.tpb.coinz.dagger.component.*
import com.tpb.coinz.dagger.module.*
import timber.log.Timber


class App : Application() {

    val homeComponent: HomeComponent by lazy {
        DaggerHomeComponent.builder()
                .loaderModule(LoaderModule())
                .locationModule(LocationModule(this))
                .storeModule(StoreModule(this))
                .backendModule(BackendModule())
                .build()
    }
    val mapComponent: MapComponent by lazy {
        DaggerMapComponent.builder()
                .loaderModule(LoaderModule())
                .locationModule(LocationModule(this))
                .storeModule(StoreModule(this))
                .connectivityModule(ConnectivityModule(this))
                .build()
    }
    val bankComponent: BankComponent by lazy {
        DaggerBankComponent.builder()
                .connectivityModule(ConnectivityModule(this))
                .backendModule(BackendModule())
                .build()
    }
    val threadsComponent: ThreadsComponent by lazy {
        DaggerThreadsComponent.builder()
                .connectivityModule(ConnectivityModule(this))
                .chatModule(ChatModule())
                .build()
    }
    val threadComponent: ThreadComponent by lazy {
        DaggerThreadComponent.builder()
                .connectivityModule(ConnectivityModule(this))
                .chatModule(ChatModule())
                .build()
    }

    override fun onCreate() {
        super.onCreate()
        init()
    }

    private fun init() {
        FirebaseApp.initializeApp(this)
        Mapbox.getInstance(this, "pk.eyJ1IjoidHBiMTkwOCIsImEiOiJjam1vd25pZm0xNWQzM3ZvZWtpZ3hmdmQ5In0.YMMSu09MMG3QPZ4m6_zndQ")
        Timber.plant(Timber.DebugTree())
    }

}