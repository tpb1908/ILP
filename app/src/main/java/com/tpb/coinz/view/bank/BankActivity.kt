package com.tpb.coinz.view.bank

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.tpb.coinz.App
import com.tpb.coinz.R
import com.tpb.coinz.data.coin.Coin
import kotlinx.android.synthetic.main.activity_bank.*
import timber.log.Timber

class BankActivity : AppCompatActivity() {

    private lateinit var vm: BankViewModel

    private val adapter = CoinRecyclerAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank)

        initViews()
        bindViewModel()

    }

    private fun bindViewModel() {
        vm = ViewModelProviders.of(this).get(BankViewModel::class.java)
        (application as App).bankComponent.inject(vm)
        vm.availableCoins.observe(this, Observer {
            Timber.i("Available coins changed $it")
            adapter.loadItems(it.first, it.second)
        })
        vm.numStillBankable.observe(this, Observer {
            adapter.setNumStillBankable(it)
        })
        vm.actions.observe(this, Observer {
            if (it is BankViewModel.BankAction.SetLoadingState) {
                bank_loading_bar.visibility = if (it.loading) View.VISIBLE else View.GONE
            }
        })
        vm.bind()
    }

    private fun initViews() {
        available_coins_recycler.adapter = adapter
        available_coins_recycler.layoutManager = LinearLayoutManager(this)
        adapter.onClick = {
            Toast.makeText(this, "${it.currency} clicked", Toast.LENGTH_LONG).show()
        }
    }

}