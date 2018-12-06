package com.tpb.coinz.view.messaging.thread

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.annotation.StyleRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tpb.coinz.R
import com.tpb.coinz.data.coin.Coin
import com.tpb.coinz.data.coin.Currency
import kotlinx.android.synthetic.main.activity_thread.*
import kotlinx.android.synthetic.main.dialog_coin_selection.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class ThreadActivity : AppCompatActivity() {

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
            Toast.makeText(this, "Cannot open thread without extra $EXTRA_THREAD", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun initViews() {
        thread_message_send.setOnClickListener {
            if (thread_message_input.text.isNullOrEmpty()) {
                Toast.makeText(this, R.string.error_empty_message, Toast.LENGTH_SHORT).show()
            } else {
                vm.postMessage(thread_message_input.text.toString())
                thread_message_input.text = null
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
                is ThreadViewModel.ThreadAction.SetLoadingState -> {
                    thread_loading_bar.visibility = if (action.isLoading) View.VISIBLE else View.GONE
                }
                is ThreadViewModel.ThreadAction.ShowCoinsDialog -> {
                    showCoinsDialog(action.coins)
                }
                is ThreadViewModel.ThreadAction.DisplayBankDialog -> {
                    showBankingRequirementDialog(action.numStillBankable)
                }
                is ThreadViewModel.ThreadAction.DisplayError -> {
                    Snackbar.make(findViewById(android.R.id.content), action.message, Snackbar.LENGTH_INDEFINITE).setAction(R.string.action_retry) {
                        action.retry()
                    }.show()
                }
            }
        })
        vm.messages.observe(this, Observer {
            adapter.setMessages(it)
            thread_messages_recycler.smoothScrollToPosition(adapter.itemCount)
        })
        adapter.isCurrentUser = vm.isCurrentUser
    }

    private fun showCoinsDialog(coins: List<Coin>) {
        Timber.i("Received collected coins $coins")
        thread_loading_bar.visibility = View.GONE
        CoinSelectionDialog(coins, this, R.style.CoinDialog, vm::transferCoin).show()
    }

    private fun showBankingRequirementDialog(numStillBankable: Int) {
        AlertDialog.Builder(this).setTitle("Banking error")
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
            setTitle("Select coin to send")
            val adapter = SimpleAdapter(context, coins.map {
                mapOf("img" to Currency.getImageId(it.currency), "value" to "${it.currency} | ${it.value}")
            },
                    R.layout.listitem_dialog_coin, arrayOf("img", "value"),
                    intArrayOf(R.id.listitem_coin_image, R.id.listitem_coin_info))
            dialog_coin_list.adapter = adapter
            dialog_coin_list.setOnItemClickListener { adapterView, view, i, l ->
                dismiss()
                //TODO: Confirm selection
                selectionListener(coins[i])
            }
        }
    }

    companion object {
        const val EXTRA_THREAD = "EXTRA_THREAD"
    }

}