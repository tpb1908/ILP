package com.tpb.coinz.messaging

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.tpb.coinz.App
import com.tpb.coinz.R

class ThreadActivity : AppCompatActivity() {

    lateinit var vm: ThreadViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thread)
        initViews()
        bindViewModel()
        if (intent.hasExtra(EXTRA_THREAD)) {
            vm.openThread(intent.getParcelableExtra(EXTRA_THREAD))
        } else {
            Toast.makeText(this, "Cannot open thread without extra $EXTRA_THREAD", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun initViews() {

    }

    private fun bindViewModel() {
        vm = ViewModelProviders.of(this).get(ThreadViewModel::class.java)
        (application as App).threadComponent.inject(vm)
        vm.bind()
    }

    companion object {
        const val EXTRA_THREAD = "EXTRA_THREAD"
    }

}