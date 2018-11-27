package com.tpb.coinz.messaging.thread

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.tpb.coinz.App
import com.tpb.coinz.R
import kotlinx.android.synthetic.main.activity_thread.*

class ThreadActivity : AppCompatActivity() {

    private lateinit var vm: ThreadViewModel

    private val adapter = ThreadRecyclerAdapter()

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
        thread_message_send.setOnClickListener {
            vm.postMessage(thread_message_input.text.toString())
            thread_message_input.text = null
        }
        thread_messages_recycler.layoutManager = LinearLayoutManager(this)
        thread_messages_recycler.adapter = adapter

    }

    private fun bindViewModel() {
        vm = ViewModelProviders.of(this).get(ThreadViewModel::class.java)
        (application as App).threadComponent.inject(vm)
        vm.bind()
        vm.actions.observe(this, Observer {
            when (it) {
                ThreadViewModel.ThreadAction.DISPLAY_LOADING -> {thread_loading_bar.visibility = View.VISIBLE}
                ThreadViewModel.ThreadAction.HIDE_LOADING -> {thread_loading_bar.visibility = View.GONE}
            }
        })
        vm.messages.observe(this, Observer {
            adapter.setMessages(it)
            thread_messages_recycler.smoothScrollToPosition(adapter.itemCount)
        })
        adapter.isCurrentUser = vm.isCurrentUser
    }

    companion object {
        const val EXTRA_THREAD = "EXTRA_THREAD"
    }

}