package com.tpb.coinz

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tpb.coinz.data.ConnectionLiveData
import com.tpb.coinz.data.chat.ChatCollection
import com.tpb.coinz.data.chat.FireStoreChatCollection
import com.tpb.coinz.data.coin.bank.CoinBank
import com.tpb.coinz.data.coin.bank.FireStoreCoinBank
import com.tpb.coinz.data.coin.collection.CoinCollection
import com.tpb.coinz.data.coin.collection.CoinCollector
import com.tpb.coinz.data.coin.collection.CoinCollectorImpl
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
import com.tpb.coinz.view.scoreboard.ScoreboardViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.experimental.builder.viewModel
import org.koin.dsl.module.module

/**
 * Commonly used data classes
 */
val commonModule = module {
    single<ConfigProvider> { ConstantConfigProvider }
    single<UserCollection> { FireBaseUserCollection(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance()) }
    single { ConnectionLiveData(androidContext()) }
    single<LocationProvider> { GMSLocationProvider(androidContext()) }
}

/**
 * Classes used in loading and storing maps
 */
val mapModule = module {
    single<MapStore> { SharedPrefsMapStore(androidContext().getSharedPreferences("mapstorage", Context.MODE_PRIVATE)) }
    single<MapLoader> { MapDownloader() }
}

/**
 * Classes used in collection of coins
 */
val coinCollectionModule = module {
    single<CoinCollector> {
        CoinCollectorImpl(get(), get(), get(), get(), get(), get())
    }
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
/**
 * ViewModels are automatically instantiated with constructor injection
 */
val viewModelModule = module {
    viewModel<HomeViewModel>()
    viewModel<MapViewModel>()
    viewModel<ThreadViewModel>()
    viewModel<ThreadsViewModel>()
    viewModel<BankViewModel>()
    viewModel<ScoreboardViewModel>()
}