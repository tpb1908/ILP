package com.tpb.coinz.messaging

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.tpb.coinz.App
import com.tpb.coinz.R
import com.tpb.coinz.SimpleTextWatcher
import com.tpb.coinz.data.backend.ChatCollection
import kotlinx.android.synthetic.main.activity_messages.*
import timber.log.Timber

class MessagesActivity : AppCompatActivity() {

    private lateinit var vm: MessagesViewModel

    private val adapter = ThreadsRecyclerAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)
        (application as App).messagesComponent.inject(this)
        initViews()
        bindViewModel()
    }

    private fun initViews() {
        fab_add_chat.setOnClickListener(addChatClick)
        messages_recycler.adapter = adapter
        messages_recycler.layoutManager = LinearLayoutManager(this)
        adapter.onClick = this::openThread
    }

    private fun openThread(thread: ChatCollection.Thread) {
        val intent = Intent(this@MessagesActivity, ThreadActivity::class.java)
        intent.putExtra(ThreadActivity.EXTRA_THREAD, thread)
        startActivity(intent)
    }

    private val addChatClick = View.OnClickListener { _ ->
        val edit = AutoCompleteTextView(this)
        val lp = LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                LinearLayoutCompat.LayoutParams.MATCH_PARENT)
        edit.layoutParams = lp

        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line)
        adapter.setNotifyOnChange(true)
        edit.setAdapter(adapter)
        edit.addTextChangedListener(object: SimpleTextWatcher() {
            override fun onTextChanged(text: String) {
                vm.searchUsers(text)
            }
        })
        vm.userSearchResults.observe(this, Observer { users ->
            adapter.clear()
            adapter.addAll(users.map { it.email })
        })
        AlertDialog.Builder(this)
                .setTitle("Email")
                .setMessage("Enter email")
                .setView(edit)
                .setPositiveButton("OK") { _, _ ->
                    vm.createChat(edit.text.toString())
                    vm.userSearchResults.removeObservers(this)
                }.show()
    }

    private fun bindViewModel() {
        vm = ViewModelProviders.of(this).get(MessagesViewModel::class.java)
        (application as App).messagesComponent.inject(vm)
        vm.bind()

        vm.threadIntents.observe(this, Observer {
            openThread(it)
        })

        vm.threads.observe(this, Observer {
            adapter.setThreads(it)
        })
    }

}