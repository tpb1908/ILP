package com.tpb.coinz.messaging.threads

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.tpb.coinz.App
import com.tpb.coinz.R
import com.tpb.coinz.SimpleTextWatcher
import com.tpb.coinz.data.backend.ChatCollection
import com.tpb.coinz.messaging.thread.ThreadActivity
import kotlinx.android.synthetic.main.activity_messages.*
import timber.log.Timber

class ThreadsActivity : AppCompatActivity() {

    private lateinit var vm: ThreadsViewModel

    private val adapter = ThreadsRecyclerAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)
        (application as App).threadsComponent.inject(this)
        initViews()
        bindViewModel()
    }

    private fun initViews() {
        fab_add_chat.setOnClickListener(addChatClick)
        messages_recycler.adapter = adapter
        messages_recycler.layoutManager = LinearLayoutManager(this)
        adapter.onClick = this::openThread
    }

    private fun bindViewModel() {
        vm = ViewModelProviders.of(this).get(ThreadsViewModel::class.java)
        (application as App).threadsComponent.inject(vm)
        vm.bind()

        vm.threadIntents.observe(this, Observer {
            openThread(it)
        })

        vm.threads.observe(this, Observer {
            Timber.i("Threads observed $it")
            adapter.setThreads(it)
        })
        vm.actions.observe(this, Observer {
            if (it is ThreadsViewModel.ThreadsAction.SetLoadingState) {
                messages_loading_bar.visibility = if (it.loading) View.VISIBLE else View.GONE
            }
        })
    }

    private fun openThread(thread: ChatCollection.Thread) {
        val intent = Intent(this@ThreadsActivity, ThreadActivity::class.java)
        intent.putExtra(ThreadActivity.EXTRA_THREAD, thread)
        startActivity(intent)
    }

    private val addChatClick = View.OnClickListener {
        val edit = AutoCompleteTextView(this)
        val container = FrameLayout(this)
        val params = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.marginStart = resources.getDimensionPixelSize(R.dimen.dialog_margin)
        params.marginEnd = resources.getDimensionPixelSize(R.dimen.dialog_margin)
        edit.layoutParams = params
        container.addView(edit)

        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line)
        adapter.setNotifyOnChange(true)
        edit.setAdapter(adapter)
        edit.addTextChangedListener(object : SimpleTextWatcher() {
            override fun onTextChanged(text: String) {
                vm.searchUsers(text)
            }
        })
        vm.userSearchResults.observe(this, Observer { users ->
            adapter.clear()
            adapter.addAll(users.map { user -> user.email })
        })
        edit.setHint(R.string.hint_chat_creation_email)
        AlertDialog.Builder(this)
                .setTitle(R.string.dialog_chat_creation_title)
                .setView(container)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    vm.createChat(edit.text.toString())
                    vm.userSearchResults.removeObservers(this)
                }.show()
    }

}