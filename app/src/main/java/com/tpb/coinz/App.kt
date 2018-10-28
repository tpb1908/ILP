package com.tpb.coinz

import android.app.Application
import androidx.core.app.ActivityCompat.startActivityForResult
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.mapbox.mapboxsdk.Mapbox

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        init()
    }

    private fun init() {
        Mapbox.getInstance(this, "pk.eyJ1IjoidHBiMTkwOCIsImEiOiJjam1vd25pZm0xNWQzM3ZvZWtpZ3hmdmQ5In0.YMMSu09MMG3QPZ4m6_zndQ")
    }

}