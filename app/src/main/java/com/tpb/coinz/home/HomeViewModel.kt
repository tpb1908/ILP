package com.tpb.coinz.home

import android.util.Log
import com.firebase.ui.auth.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.tpb.coinz.base.BaseViewModel

class HomeViewModel : BaseViewModel<HomeNavigator>() {

    private var user: FirebaseUser? = null

    override fun init() {
        user = FirebaseAuth.getInstance().currentUser
        Log.i(this::class.toString(), "User $user")
        if (user == null) {
            navigator.get()?.beginLoginFlow()
        }

    }
}