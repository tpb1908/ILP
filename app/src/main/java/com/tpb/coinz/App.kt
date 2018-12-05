package com.tpb.coinz

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Looper
import android.util.Log
import com.crashlytics.android.Crashlytics
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.mapbox.mapboxsdk.Mapbox
import com.tpb.coinz.data.ConnectionLiveData
import com.tpb.coinz.data.chat.ChatCollection
import com.tpb.coinz.data.chat.FireStoreChatCollection
import com.tpb.coinz.data.coin.CoinCollector
import com.tpb.coinz.data.coin.bank.CoinBank
import com.tpb.coinz.data.coin.bank.FireStoreCoinBank
import com.tpb.coinz.data.coin.collection.CoinCollection
import com.tpb.coinz.data.coin.collection.FireStoreCoinCollection
import com.tpb.coinz.data.coin.loading.MapDownloader
import com.tpb.coinz.data.coin.loading.MapLoader
import com.tpb.coinz.data.coin.storage.MapStore
import com.tpb.coinz.data.coin.storage.SharedPrefsMapStore
import com.tpb.coinz.data.config.ConfigProvider
import com.tpb.coinz.data.config.ConstantConfigProvider
import com.tpb.coinz.data.location.GMSLocationProvider
import com.tpb.coinz.data.location.LocationProvider
import com.tpb.coinz.data.location.background.ForegroundLocationService
import com.tpb.coinz.data.users.FireBaseUserCollection
import com.tpb.coinz.data.users.UserCollection
import com.tpb.coinz.view.bank.BankViewModel
import com.tpb.coinz.view.home.HomeViewModel
import com.tpb.coinz.view.map.MapViewModel
import com.tpb.coinz.view.messaging.thread.ThreadViewModel
import com.tpb.coinz.view.messaging.threads.ThreadsViewModel
import org.koin.android.ext.android.startKoin
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import timber.log.Timber


class App : Application() {


    override fun onCreate() {
        super.onCreate()
        init()
    }


    private fun init() {
        Mapbox.getInstance(this, "pk.eyJ1IjoidHBiMTkwOCIsImEiOiJjam1vd25pZm0xNWQzM3ZvZWtpZ3hmdmQ5In0.YMMSu09MMG3QPZ4m6_zndQ")
        Timber.plant(if (BuildConfig.DEBUG) Timber.DebugTree() else CrashlyticsTree)
        val firestore = FirebaseFirestore.getInstance()
        val chatModule = module {
            single<ChatCollection> { FireStoreChatCollection(firestore) }
        }
        val configModule = module {
            single<ConfigProvider> { ConstantConfigProvider }
        }
        val coinBankModule = module {
            single<CoinBank> {
                FireStoreCoinBank(
                        getSharedPreferences("coinbank", Context.MODE_PRIVATE),
                        firestore,
                        get()
                )
            }
        }
        val coinCollectionModule = module {
            single {
                Timber.i("Instantiating coin collector. On main thread? ${Looper.myLooper() == Looper.getMainLooper()}")
                CoinCollector(get(), get(), get(), get()) }
            single<CoinCollection> { FireStoreCoinCollection(firestore) }
        }
        val connectivityModule = module {
            single { ConnectionLiveData(this@App) }
        }
        val locationModule = module {
            single<LocationProvider> { GMSLocationProvider(this@App) }
        }
        val mapModule = module {
            single<MapStore> { SharedPrefsMapStore(getSharedPreferences("map_storage_prefs", Context.MODE_PRIVATE)) }
            single<MapLoader> { MapDownloader() }
        }
        val userModule = module {
            single<UserCollection> { FireBaseUserCollection(firestore) }
        }
        val viewModelModule = module {
            viewModel { HomeViewModel(get(), get()) }
            viewModel { MapViewModel(get(), get(), get()) }
            viewModel { ThreadViewModel(get(), get(), get(), get()) }
            viewModel { ThreadsViewModel(get(), get()) }
            viewModel { BankViewModel(get(), get()) }
        }
        startKoin(this, listOf(viewModelModule,
                configModule,
                connectivityModule,
                locationModule,
                mapModule,
                userModule,
                chatModule,
                coinBankModule, coinCollectionModule),
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
