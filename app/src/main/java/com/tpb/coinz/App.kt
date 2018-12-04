package com.tpb.coinz

import android.app.Application
import android.util.Log
import com.crashlytics.android.Crashlytics
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mapbox.mapboxsdk.Mapbox
import com.tpb.coinz.dagger.component.*
import com.tpb.coinz.dagger.module.*
import timber.log.Timber


class App : Application() {

    val activityComponent: ActivityComponent by lazy {
        DaggerActivityComponent.builder()
                .connectivityModule(ConnectivityModule(this))
                .build()
    }

    val homeComponent: HomeComponent by lazy {
        DaggerHomeComponent.builder()
                .locationModule(LocationModule(this))
                .mapModule(MapModule(this))
                .build()
    }
    val mapComponent: MapComponent by lazy {
        DaggerMapComponent.builder()
                .locationModule(LocationModule(this))
                .connectivityModule(ConnectivityModule(this))
                .mapModule(MapModule(this))
                .build()
    }
    val bankComponent: BankComponent by lazy {
        DaggerBankComponent.builder()
                .coinBankModule(CoinBankModule(this))
                .build()
    }
    val threadsComponent: ThreadsComponent by lazy {
        DaggerThreadsComponent.builder()
                .connectivityModule(ConnectivityModule(this))
                .build()
    }
    val threadComponent: ThreadComponent by lazy {
        DaggerThreadComponent.builder()
                .connectivityModule(ConnectivityModule(this))
                .build()
    }

    override fun onCreate() {
        super.onCreate()
        init()
    }

    private fun init() {
        FirebaseApp.initializeApp(this)
        Mapbox.getInstance(this, "pk.eyJ1IjoidHBiMTkwOCIsImEiOiJjam1vd25pZm0xNWQzM3ZvZWtpZ3hmdmQ5In0.YMMSu09MMG3QPZ4m6_zndQ")
        Timber.plant(if (BuildConfig.DEBUG) Timber.DebugTree() else CrashlyticsTree)


        FirebaseFirestore.getInstance().collection("threads").whereEqualTo("creator", "VDLK8igUKmMqpx9O2DcnEyovpa92").addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            Timber.i("Simple query updated")
        }
        FirebaseFirestore.getInstance().collection("threads").whereEqualTo("creator", "VDLK8igUKmMqpx9O2DcnEyovpa92").orderBy("last_updated", Query.Direction.DESCENDING).limit(10).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            Timber.i("Compound query updated")
        }
    }

    // Timber tree which only logs errors and warnings
    private object CrashlyticsTree : Timber.Tree() {

        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            when (priority) {
                Log.ERROR -> {
                    Crashlytics.logException(t)
                }
                Log.WARN -> {
                    Crashlytics.log(priority, tag, message)
                }
            }
        }
    }
}
