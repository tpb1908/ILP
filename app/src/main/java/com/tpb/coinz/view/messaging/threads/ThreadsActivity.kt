package com.tpb.coinz.view.messaging.threads

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tpb.coinz.R
import com.tpb.coinz.SimpleTextWatcher
import com.tpb.coinz.data.chat.Thread
import com.tpb.coinz.view.base.BaseActivity
import com.tpb.coinz.view.messaging.thread.ThreadActivity
import kotlinx.android.synthetic.main.activity_messages.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class ThreadsActivity : BaseActivity() {

    val vm: ThreadsViewModel by viewModel()

    private val adapter = ThreadsRecyclerAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)
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
        vm.bind()

        vm.threadIntents.observe(this, Observer {
            openThread(it)
        })

        vm.threads.observe(this, Observer {
            Timber.i("Threads observed $it")
            adapter.setThreads(it)
        })
        vm.actions.observe(this, Observer { action ->
            if (action is ThreadsViewModel.ThreadsAction.DisplayError) {
                Snackbar.make(
                        findViewById(android.R.id.content),
                        action.message,
                        Snackbar.LENGTH_LONG).setAction(R.string.action_retry) {
                    action.retry()
                }.show()
            } else if (action is ThreadsViewModel.ThreadsAction.DisplayMessage) {
                Toast.makeText(this, getString(action.message, *action.formatArgs), Toast.LENGTH_SHORT).show()
            }
        })
        vm.loadingState.observe(this, Observer {
            messages_loading_bar.visibility = if (it) View.VISIBLE else View.GONE
        })
    }

    private fun openThread(thread: Thread) {
        startActivity(ThreadActivity.createIntent(this, thread))
    }

    private val addChatClick = View.OnClickListener {
        // Create a simple dialog with an AutoCompleteTextview
        val edit = AutoCompleteTextView(this)
        val container = FrameLayout(this)
        val params = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.marginStart = resources.getDimensionPixelSize(R.dimen.dialog_margin)
        params.marginEnd = resources.getDimensionPixelSize(R.dimen.dialog_margin)
        edit.layoutParams = params
        container.addView(edit)

        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line)
        adapter.setNotifyOnChange(true) // changes to adapter data should call notifyDataSetChanged
        edit.addTextChangedListener(object : SimpleTextWatcher() {
            override fun onTextChanged(text: String) {
                vm.searchUsers(text)
            }
        })
        vm.userSearchResults.observe(this, Observer { users ->
            adapter.clear()
            adapter.addAll(users.map { user -> user.email })
        })
        edit.setAdapter(adapter)
        edit.setHint(R.string.hint_chat_creation_email)
        edit.maxLines = 1
        // Display the dialog
        AlertDialog.Builder(this)
                .setTitle(R.string.title_chat_creation_dialog)
                .setView(container)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    vm.createChat(edit.text.toString())
                    // Remove all observers belonging to this activity lifecycle (of which there is only the one above)
                    vm.userSearchResults.removeObservers(this)
                }.show()
    }

}