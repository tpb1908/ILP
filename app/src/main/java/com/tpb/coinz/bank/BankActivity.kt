package com.tpb.coinz.bank

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.tpb.coinz.R
import com.tpb.coinz.data.coins.Coin
import kotlinx.android.synthetic.main.activity_bank.*

class BankActivity : AppCompatActivity(), BankNavigator{

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
        vm.availableCoins.observe(this, Observer<Pair<List<Coin>, List<Coin>>> {
            adapter.loadItems(it.first, it.second)
        })
    }

    private fun initViews() {
        available_coins_recycler.adapter = adapter
    }

}