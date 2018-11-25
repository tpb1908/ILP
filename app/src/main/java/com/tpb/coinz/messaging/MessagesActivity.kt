package com.tpb.coinz.messaging

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.tpb.coinz.App
import com.tpb.coinz.R
import kotlinx.android.synthetic.main.activity_messages.*

class MessagesActivity : AppCompatActivity(), MessagesNavigator {

    private lateinit var vm: MessagesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)
        (application as App).messagesComponent.inject(this)
        bindViewModel()
        initViews()
    }

    private fun initViews() {
        fab_add_chat.setOnClickListener(addChatClick)
    }

    private val addChatClick = View.OnClickListener {
        val edit = AutoCompleteTextView(this)
        val lp = LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                LinearLayoutCompat.LayoutParams.MATCH_PARENT)

        edit.layoutParams = lp
        AlertDialog.Builder(this)
                .setTitle("Email")
                .setMessage("Enter email")
                .setView(edit)
                .setPositiveButton("OK") { _, _ ->
                    vm.createChat(edit.text.toString())
                }.show()
    }

    private fun bindViewModel() {
        vm = ViewModelProviders.of(this).get(MessagesViewModel::class.java)
        (application as App).messagesComponent.inject(vm)
        vm.setNavigator(this)
        vm.bind()

        vm.threadIntents.observe(this, Observer<String> {
            val intent = Intent(this@MessagesActivity, ThreadActivity::class.java)
            intent.putExtra(ThreadActivity.EXTRA_THREAD_ID, it)
            startActivity(intent)
        })
    }

}