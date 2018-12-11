package com.tpb.coinz.view.bank

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tpb.coinz.R
import com.tpb.coinz.data.config.ConfigProvider
import com.tpb.coinz.view.base.BaseActivity
import kotlinx.android.synthetic.main.activity_bank.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class BankActivity : BaseActivity() {

    private val vm: BankViewModel by viewModel()

    val config: ConfigProvider by inject()

    private val adapter = CoinRecyclerAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank)
        initViews()
        bindViewModel()

    }

    private fun bindViewModel() {
        vm.bankableCoins.observe(this, Observer {
            adapter.loadItems(it.first, it.second)
        })
        vm.rates.observe(this, Observer {
            adapter.setRates(it)
        })
        vm.actions.observe(this, Observer { action ->
            when (action) {
                is BankViewModel.BankAction.SelectionFull -> {

                }
                is BankViewModel.BankAction.DisplayError ->
                    Snackbar.make(findViewById(android.R.id.content),
                            action.message,
                            Snackbar.LENGTH_INDEFINITE).setAction(R.string.action_retry) {
                        action.retry()
                    }.show()
            }
        })
        vm.loadingState.observe(this, Observer {
            bank_loading_bar.visibility = if (it) View.VISIBLE else View.GONE
        })
        vm.numStillBankable.observe(this, Observer {
            bank_coins_text.text = resources.getQuantityString(
                    R.plurals.bank_coins_banked_info,
                    config.dailyBankLimit - it, config.dailyBankLimit - it, it)
        })
        vm.numCollectedSelected.observe(this, Observer {
            if (it == 0) {
                bank_selected_coins_text.visibility = View.GONE
            } else {
                bank_selected_coins_text.text = getString(R.string.text_selected_coins, it)
                bank_selected_coins_text.visibility = View.VISIBLE
            }

        })
        adapter.selectionManager = vm
        vm.bind()
    }

    private fun initViews() {
        available_coins_recycler.adapter = adapter
        available_coins_recycler.layoutManager = LinearLayoutManager(this)
        bank_coins_button.setOnClickListener {
            if (adapter.numSelectedCoins() > 0) {
                vm.bankCoins()
            } else {
                Toast.makeText(this, R.string.message_need_to_select_coins, Toast.LENGTH_SHORT).show()
            }
        }
    }

}