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
import com.tpb.coinz.data.config.ConfigProvider
import kotlinx.android.synthetic.main.activity_bank.*
import timber.log.Timber
import javax.inject.Inject

class BankActivity : AppCompatActivity() {

    private lateinit var vm: BankViewModel

    @Inject lateinit var config: ConfigProvider

    private val adapter = CoinRecyclerAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank)
        (application as App).activityComponent.inject(this)
        initViews()
        bindViewModel()

    }

    private fun bindViewModel() {
        vm = ViewModelProviders.of(this).get(BankViewModel::class.java)
        (application as App).bankComponent.inject(vm)
        vm.bankableCoins.observe(this, Observer {
            Timber.i("Available coins changed $it")
            adapter.loadItems(it.first, it.second)
        })
        vm.actions.observe(this, Observer {
            if (it is BankViewModel.BankAction.SetLoadingState) {
                bank_loading_bar.visibility = if (it.loading) View.VISIBLE else View.GONE
            } else if (it is BankViewModel.BankAction.SelectionFull) {

            }
        })
        vm.numStillBankable.observe(this, Observer {
            bank_coins_text.text = resources.getQuantityString(
                    R.plurals.bank_coins_banked_info,
                    config.dailyCollectionLimit-it, config.dailyCollectionLimit-it, it)
        })
        vm.numSelected.observe(this, Observer {
            //TODO: Display this text somewhere
        })
        adapter.selectionManager = vm
        vm.bind()
    }

    private fun initViews() {
        available_coins_recycler.adapter = adapter
        available_coins_recycler.layoutManager = LinearLayoutManager(this)
        adapter.onClick = {
            Toast.makeText(this, "${it.currency} clicked", Toast.LENGTH_LONG).show()
        }
        bank_coins_button.setOnClickListener {
            if (adapter.numSelectedCoins() > 0) {
                vm.bankCoins()
            } else {
                Toast.makeText(this, R.string.message_need_to_select_coins, Toast.LENGTH_SHORT).show()
            }
        }
    }

}