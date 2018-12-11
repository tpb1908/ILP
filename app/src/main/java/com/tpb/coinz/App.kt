package com.tpb.coinz

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.crashlytics.android.Crashlytics
import com.mapbox.mapboxsdk.Mapbox
import org.koin.android.ext.android.startKoin
import timber.log.Timber


class App : Application() {


    override fun onCreate() {
        super.onCreate()
        // Set theme automatically based on time of day. As we have location permissions
        // this will be determined by local sunrise/set times
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO)
        init()
    }


    private fun init() {
        // Initialise MapBox with key in gradle.properties
        Mapbox.getInstance(this, BuildConfig.MapBoxAPIKey)
        Timber.plant(if (BuildConfig.DEBUG) Timber.DebugTree() else CrashlyticsTree)

        startKoin(this, listOf(viewModelModule,
                mapModule,
                commonModule,
                chatModule,
                coinBankModule,
                coinCollectionModule,
                scoreboardModule),
                logger = TimberKoinLogger)

    }

    /**
     * Logs Koin events via Timber
     */
    private object TimberKoinLogger : org.koin.log.Logger {
        override fun debug(msg: String) = Timber.d(msg)

        override fun err(msg: String) = Timber.e(msg)

        override fun info(msg: String) = Timber.i(msg)

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
