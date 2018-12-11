package com.tpb.coinz.view.base

import androidx.appcompat.app.AppCompatActivity
import com.tpb.coinz.data.location.background.ForegroundLocationService

abstract class BaseActivity : AppCompatActivity() {

    override fun onStart() {
        super.onStart()
        ForegroundLocationService.isActivityInForeground = true
    }

    override fun onPause() {
        super.onPause()
        ForegroundLocationService.isActivityInForeground = false
    }
}