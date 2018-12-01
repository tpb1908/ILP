package com.tpb.coinz.data.coin.collection

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.mapbox.mapboxsdk.geometry.LatLng
import com.tpb.coinz.Result
import com.tpb.coinz.data.coin.Coin
import com.tpb.coinz.data.coin.Currency
import com.tpb.coinz.data.users.User
import timber.log.Timber


class FireStoreCoinCollection(private val store: FirebaseFirestore) : CoinCollection {

    private val collected = "collected"
    private val scoreboardAll = "all"
    private val total = "total"
    private val coins = "coins"

    private inline fun coins(user: User): CollectionReference = store.collection(collected).document(user.uid).collection(coins)

    override fun collectCoin(user: User, coin: Coin) {
        Timber.i("Collecting ${toMap(coin)} for $user")
        //TODO: Check result and callback
        coins(user).add(toMap(coin))
    }

    private fun updateScoreboard(id: String, coin: Coin) {
        val userScore = store.collection(scoreboardAll).document(id)
        store.runTransaction {
            val snapshot = it.get(userScore)
            var score = snapshot.getDouble(total) ?: 0.0
            score += coin.value
            it.set(userScore, mapOf(total to score), SetOptions.merge())
            // Set with merge will create document if it doesn't exist
            //it.update(userScore, total, score)
        }.addOnSuccessListener {
            Timber.i("Successfully updated scoreboard for $id")
        }.addOnFailureListener {
            Timber.e(it, "Failed to update scoreboard for $id")
        }
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
                callback(Result.Value(coins))
            } else {
                Timber.e(qs.exception, "Query unsuccessful")
                callback(Result.None)
            }
        }
    }

    override fun getBankableCoins(user: User, callback: (Result<List<Coin>>) -> Unit) {
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

    override fun transferCoin(from: User, to: User, coin: Coin) {
        // Unfortunately there is no way to move a document in one operation
        // Instead we load the coin, changed received to true, write the coin to the receiver, and then remove the original

        coins(from).whereEqualTo("id", coin.id).get().addOnCompleteListener { getTask ->
            Timber.i("Transferring coin $coin from $from to $to")
            if (getTask.result?.documents?.isEmpty() == false) {
                // If there's more than one result, something has gone horribly wrong
                getTask.result?.documents?.first()?.let { ds ->
                    ds.data?.apply {
                        coins(to).add(toMap(coin.copy(received = true))).addOnCompleteListener { addTask ->
                            if (addTask.isSuccessful) {
                                Timber.i("Transferred coin. Deleting original")
                                ds.reference.delete()
                            } else {
                                //TODO: Error callback
                                Timber.e(addTask.exception, "Couldn't transfer coin")
                            }
                        }
                    }

                }
            } else {
                // In theory this can only happen if the user opens the send dialog on one device, then on another device,
                // and then attempts to send the same coin from both
                Timber.e("Coin to send doesn't exist ${getTask.result}, ${getTask.result?.documents}")
                //TODO Coin doesn't exist error
            }
        }
    }

    private fun toMap(coin: Coin): HashMap<String, Any> {
        return hashMapOf(
                "id" to coin.id,
                "value" to coin.value,
                "currency" to coin.currency.name,
                "markerSymbol" to coin.markerSymbol,
                "markerColor" to coin.markerColor,
                "latitude" to coin.location.latitude,
                "longitude" to coin.location.longitude,
                "banked" to coin.banked,
                "received" to coin.received)

    }

    private fun fromMap(map: MutableMap<String, Any>): Coin {
        return Coin(map["id"] as String, map["value"] as Double, Currency.fromString(map["currency"] as String),
                (map["markerSymbol"] as Long).toInt(), (map["markerColor"] as Long).toInt(),
                LatLng(map["latitude"] as Double, map["longitude"] as Double),
                map["banked"] as Boolean, map["received"] as Boolean)
    }

}