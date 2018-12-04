package com.tpb.coinz.data.coin.collection

import com.google.firebase.firestore.FirebaseFirestore
import com.tpb.coinz.data.chat.Message
import com.tpb.coinz.data.coin.Coin
import com.tpb.coinz.data.coin.FireStoreCoinManager
import com.tpb.coinz.data.users.User
import com.tpb.coinz.data.util.CoinzException
import com.tpb.coinz.data.util.Conversion
import com.tpb.coinz.data.util.Conversion.fromMap
import com.tpb.coinz.data.util.Conversion.toMap
import com.tpb.coinz.orElse
import timber.log.Timber
import java.lang.Exception


class FireStoreCoinCollection(store: FirebaseFirestore) : FireStoreCoinManager(store), CoinCollection {


    override fun collectCoin(user: User, coin: Coin) {
        Timber.i("Collecting ${toMap(coin)} for $user")
        //TODO: Check result and callback
        coins(user).add(toMap(coin))
    }


    override fun getCollectedCoins(user: User, callback: (Result<List<Coin>>) -> Unit) {
        Timber.i("Loading collected coins for $user")
        coins(user).get().addOnCompleteListener { qs ->
            if (qs.isSuccessful) {
                val coins = mutableListOf<Coin>()
                //TODO: Query for banked or not banked coins
                qs.result?.documents?.forEach { ds ->
                    ds.data?.let { coins.add(fromMap(it)) }
                }
                Timber.i("Collected coins $coins")
                callback(Result.success(coins))
            } else {
                Timber.e(qs.exception, "Query unsuccessful")
                callback(Result.failure(CoinzException.UnknownException()))
            }
        }
    }

    override fun transferCoin(from: User, to: User, coin: Coin, callback: (Result<Message>) -> Unit) {
        // Unfortunately there is no way to move a document in one operation
        // Instead we load the coin, changed received to true, write the coin to the receiver, and then remove the original

        coins(from).whereEqualTo("id", coin.id).get().addOnCompleteListener { getTask ->
            Timber.i("Transferring coin $coin from $from to $to")
            // If there's more than one result, something has gone horribly wrong
            getTask.result?.documents?.firstOrNull()?.let { ds ->
                ds.data?.apply {
                    coins(to).add(toMap(coin.copy(received = true))).addOnCompleteListener { addTask ->
                        if (addTask.isSuccessful) {
                            Timber.i("Transferred coin. Deleting original")
                            ds.reference.delete()
                            callback(Result.success(Message(System.currentTimeMillis(), from, "", coin)))
                        } else {
                            //TODO: Error callback
                            Timber.e(addTask.exception, "Couldn't transfer coin")
                            callback(Result.failure(CoinzException.UnknownException()))
                        }
                    }
                }
            }.orElse {
                // In theory this can only happen if the user opens the send dialog on one device, then on another device,
                // and then attempts to send the same coin from both
                Timber.e("Coin to send doesn't exist ${getTask.result}, ${getTask.result?.documents}")
                //TODO Coin doesn't exist error
            }
        }
    }
}