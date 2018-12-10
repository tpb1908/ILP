package com.tpb.coinz

import android.content.Context
import android.os.Looper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tpb.coinz.data.ConnectionLiveData
import com.tpb.coinz.data.chat.ChatCollection
import com.tpb.coinz.data.chat.FireStoreChatCollection
import com.tpb.coinz.data.coin.CoinCollectorImpl
import com.tpb.coinz.data.coin.bank.CoinBank
import com.tpb.coinz.data.coin.bank.FireStoreCoinBank
import com.tpb.coinz.data.coin.collection.CoinCollection
import com.tpb.coinz.data.coin.collection.CoinCollector
import com.tpb.coinz.data.coin.collection.FireStoreCoinCollection
import com.tpb.coinz.data.coin.loading.MapDownloader
import com.tpb.coinz.data.coin.loading.MapLoader
import com.tpb.coinz.data.coin.scoreboard.FireStoreScoreboard
import com.tpb.coinz.data.coin.scoreboard.Scoreboard
import com.tpb.coinz.data.coin.storage.MapStore
import com.tpb.coinz.data.coin.storage.SharedPrefsMapStore
import com.tpb.coinz.data.config.ConfigProvider
import com.tpb.coinz.data.config.ConstantConfigProvider
import com.tpb.coinz.data.location.GMSLocationProvider
import com.tpb.coinz.data.location.LocationProvider
import com.tpb.coinz.data.users.FireBaseUserCollection
import com.tpb.coinz.data.users.UserCollection
import com.tpb.coinz.view.bank.BankViewModel
import com.tpb.coinz.view.home.HomeViewModel
import com.tpb.coinz.view.map.MapViewModel
import com.tpb.coinz.view.messaging.thread.ThreadViewModel
import com.tpb.coinz.view.messaging.threads.ThreadsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import timber.log.Timber


val commonModule = module {
    single<ConfigProvider> { ConstantConfigProvider }
    single<UserCollection> { FireBaseUserCollection(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance()) }
    single { ConnectionLiveData(androidContext()) }
    single<LocationProvider> { GMSLocationProvider(androidContext()) }
}

val mapModule = module {
    single<MapStore> { SharedPrefsMapStore(androidContext().getSharedPreferences("map_storage_prefs", Context.MODE_PRIVATE)) }
    single<MapLoader> { MapDownloader() }
}

val coinCollectionModule = module {
    single<CoinCollector> {
        Timber.i("Instantiating coin collector. On main thread? ${Looper.myLooper() == Looper.getMainLooper()}")
        CoinCollectorImpl(get(), get(), get(), get(), get(), get()) }
    single<CoinCollection> { FireStoreCoinCollection(FirebaseFirestore.getInstance()) }
}

val chatModule = module {
    single<ChatCollection> { FireStoreChatCollection(FirebaseFirestore.getInstance()) }
}

val coinBankModule = module {
    single<CoinBank> {
        FireStoreCoinBank(
                androidContext().getSharedPreferences("coinbank", Context.MODE_PRIVATE),
                FirebaseFirestore.getInstance(),
                get()
        )
    }
}
val scoreboardModule = module {
    single<Scoreboard> { FireStoreScoreboard(FirebaseFirestore.getInstance())}
}
val viewModelModule = module(override = true) {
    viewModel { HomeViewModel(get(), get()) }
    viewModel { MapViewModel(get()) }
    viewModel { ThreadViewModel(get(), get(), get(), get()) }
    viewModel { ThreadsViewModel(get(), get()) }
    viewModel { BankViewModel(get(), get(), get(), get()) }
}