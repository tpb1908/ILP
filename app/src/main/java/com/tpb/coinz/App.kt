package com.tpb.coinz

import android.app.Application
import android.util.Log
import com.crashlytics.android.Crashlytics
import com.mapbox.mapboxsdk.Mapbox
import org.koin.android.ext.android.startKoin
import timber.log.Timber


class App : Application() {


    override fun onCreate() {
        super.onCreate()
        init()
    }


    private fun init() {
        Mapbox.getInstance(this, "pk.eyJ1IjoidHBiMTkwOCIsImEiOiJjam1vd25pZm0xNWQzM3ZvZWtpZ3hmdmQ5In0.YMMSu09MMG3QPZ4m6_zndQ")
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
