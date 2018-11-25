package com.tpb.coinz.messaging

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class ThreadActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    companion object {
        const val EXTRA_THREAD_ID = "EXTRA_THREAD_ID"
    }

}