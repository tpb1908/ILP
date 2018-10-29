package com.tpb.coinz.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.tpb.coinz.base.BaseViewModel
import com.tpb.coinz.data.CoinDownloader
import java.util.*

class HomeViewModel(application: Application) : BaseViewModel<HomeNavigator>(application) {

    private var fbUser: FirebaseUser? = null

    val user = MutableLiveData<FirebaseUser>()

    override fun init() {
        fbUser = FirebaseAuth.getInstance().currentUser
        Log.i(this::class.toString(), "User $fbUser")
        if (fbUser == null) {
            navigator.get()?.beginLoginFlow()
        } else {
            user.postValue(fbUser)
        }
        downloadCoins()
    }

    fun userLoggedIn() {
        fbUser = FirebaseAuth.getInstance().currentUser
        user.postValue(fbUser)
    }

    fun userLoginFailed() {
        navigator.get()?.beginLoginFlow()
    }

    fun downloadCoins() {
        CoinDownloader({Log.i("Coins", "Coins are $it")}).execute(Calendar.getInstance())
    }

}