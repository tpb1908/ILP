package com.tpb.coinz.view.messaging.thread

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.annotation.StyleRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tpb.coinz.R
import com.tpb.coinz.data.chat.Thread
import com.tpb.coinz.data.coin.Coin
import com.tpb.coinz.view.base.BaseActivity
import kotlinx.android.synthetic.main.activity_thread.*
import kotlinx.android.synthetic.main.dialog_coin_selection.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class ThreadActivity : BaseActivity() {

    private val vm: ThreadViewModel by viewModel()

    private val adapter = ThreadRecyclerAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thread)

        initViews()
        bindViewModel()
        if (intent.hasExtra(EXTRA_THREAD)) {
            vm.openThread(intent.getParcelableExtra(EXTRA_THREAD))
        } else {
            // This should't happen if createIntent is used
            Toast.makeText(this, "Cannot open thread without extra $EXTRA_THREAD", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun initViews() {
        thread_message_send.setOnClickListener {
            // Check that a message exists, and ask vm to send it if so
            if (thread_message_input.text.isNullOrEmpty()) {
                Toast.makeText(this, R.string.error_empty_message, Toast.LENGTH_SHORT).show()
            } else {
                vm.postTextMessage(thread_message_input.text.toString())
            }
        }
        thread_add_coin.setOnClickListener {
            vm.loadCoinsForTransfer()
        }

        thread_messages_recycler.layoutManager = LinearLayoutManager(this)
        thread_messages_recycler.adapter = adapter
    }

    private fun bindViewModel() {
        vm.bind()
        vm.actions.observe(this, Observer { action ->
            when (action) {
                is ThreadViewModel.ThreadAction.ShowCoinsDialog -> {
                    // Callback after calling vm.loadCoinsForTransfer
                    showCoinsDialog(action.coins)
                }
                is ThreadViewModel.ThreadAction.DisplayBankDialog -> {
                    showBankingRequirementDialog(action.numStillBankable)
                }
                is ThreadViewModel.ThreadAction.DisplayError -> {
                    Snackbar.make(findViewById(android.R.id.content),
                            action.message,
                            Snackbar.LENGTH_LONG
                    ).setAction(R.string.action_retry) {
                        action.retry()
                    }.show()
                }
                is ThreadViewModel.ThreadAction.ClearMessageEntry -> {
                    // Message has sent successfully, so we can clear the EditText
                    thread_message_input.text = null
                }
            }
        })
        vm.loadingState.observe(this, Observer {
            thread_loading_bar.visibility = if (it) View.VISIBLE else View.GONE
        })
        vm.messages.observe(this, Observer {
            adapter.setMessages(it)
            thread_messages_recycler.smoothScrollToPosition(adapter.itemCount)
        })
        adapter.isCurrentUser = vm.isCurrentUser
    }

    private fun showCoinsDialog(coins: List<Coin>) {
        Timber.i("Received collected coins $coins")
        CoinSelectionDialog(coins, this, R.style.CoinDialog, vm::transferCoin).show()
    }

    private fun showBankingRequirementDialog(numStillBankable: Int) {
        AlertDialog.Builder(this).setTitle(R.string.title_banking_error_dialog)
                .setMessage(resources.getQuantityString(R.plurals.error_spare_change, numStillBankable, numStillBankable))
                .create().show()
    }

    private class CoinSelectionDialog(val coins: List<Coin>,
                                      context: Context,
                                      @StyleRes style: Int,
                                      val selectionListener: (Coin) -> Unit) : AppCompatDialog(context, style) {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.dialog_coin_selection)
            setTitle(R.string.title_coin_send_dialog)

            // SimpleAdapter requires a map of data to corresponding layout elements
            // which is why listitem_dialog_coin uses an ImageView and a TextView rather than a TextView and
            // compound drawable
            val adapter = SimpleAdapter(context, coins.map {
                mapOf("img" to it.currency.img, "value" to "${it.currency} | ${it.value}")
            },
                    R.layout.listitem_dialog_coin, arrayOf("img", "value"),
                    intArrayOf(R.id.listitem_coin_image, R.id.listitem_coin_info))
            dialog_coin_list.adapter = adapter
            dialog_coin_list.setOnItemClickListener { _, _, index, _ ->
                dismiss()
                selectionListener(coins[index])
            }
        }
    }

    companion object {
        // Intent extra key
        const val EXTRA_THREAD = "EXTRA_THREAD"

        /**
         * Creates a valid [Intent] for launching [ThreadActivity]
         */
        fun createIntent(context: Context, thread: Thread): Intent {
            val intent = Intent(context, ThreadActivity::class.java)
            intent.putExtra(ThreadActivity.EXTRA_THREAD, thread)
            return intent
        }
    }

}