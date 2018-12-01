package com.tpb.coinz.data.coin.bank

import com.google.firebase.firestore.FirebaseFirestore
import com.tpb.coinz.Result
import com.tpb.coinz.data.coin.Coin
import com.tpb.coinz.data.coin.FireStoreCoin
import com.tpb.coinz.data.users.User
import timber.log.Timber

class FireStoreCoinBank(store: FirebaseFirestore) : FireStoreCoin(store), CoinBank {

    override fun getBankableCoins(user: User, callback: (Result<List<Coin>>) -> Unit) {
        //TODO: Realtime?
        coins(user).whereEqualTo("banked", false).get().addOnCompleteListener { qs ->
            if (qs.isSuccessful) {
                val coins = mutableListOf<Coin>()
                qs.result?.documents?.forEach { ds ->
                    ds.data?.let { coins.add(fromMap(it)) }
                }
                Timber.i("Bankable coins $coins")
                callback(Result.Value(coins))
            } else {
                Timber.e(qs.exception, "Query unsuccessful")
                callback(Result.None)
            }
        }
    }

    override fun getNumBankable() {
    }
}