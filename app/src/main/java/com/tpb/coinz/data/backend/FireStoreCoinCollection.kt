package com.tpb.coinz.data.backend

import com.google.firebase.firestore.*
import com.tpb.coinz.data.coins.Coin
import com.mapbox.mapboxsdk.geometry.LatLng
import com.tpb.coinz.data.coins.Currency
import timber.log.Timber


class FireStoreCoinCollection(private val store: FirebaseFirestore) : CoinCollection {

    private val collected = "collected"
    private val scoreboard = "scoreboard"
    private val scoreboardAll = "all"
    private val total = "total"
    private val coins = "coins"

    private inline fun coins(user: UserCollection.User): CollectionReference = store.collection(collected).document(user.uid).collection(coins)

    override fun collectCoin(user: UserCollection.User, coin: Coin) {
        Timber.i("Collecting ${toMap(coin)} for $user")

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

    override fun getCollectedCoins(user: UserCollection.User, listener: (List<Coin>) -> Unit) {
        Timber.i("Loading collected coins for $user")
        coins(user).get().addOnCompleteListener { qs ->
            if (qs.isSuccessful) {
                val coins = mutableListOf<Coin>()
                qs.result?.documents?.forEach { ds ->
                    ds.data?.let { coins.add(fromMap(it)) }
                }
                Timber.i("Collected coins $coins")
                listener(coins)
            } else {
                Timber.e(qs.exception, "Query unsuccessful")
            }
        }
    }

    override fun transferCoin(from: UserCollection.User, to: UserCollection.User, coin: Coin) {
        coins(from).whereEqualTo("id", coin.id).get().addOnCompleteListener {
            Timber.i("Transferring coin $coin from $from to $to")
            if (it.result?.documents?.isEmpty() == false) {
                it.result?.documents?.first()?.let { ds ->
                    ds.data?.apply {
                        coins(to).add(toMap(coin.copy(received = true))).addOnCompleteListener {
                            if (it.isSuccessful) {
                                Timber.i("Transferred coin. Deleting original")
                                ds.reference.delete()
                            } else {
                                Timber.e(it.exception, "Couldn't transfer coin")
                            }
                        }
                    }

                }
            } else {
                Timber.e("Coin to send doesn't exist ${it.result}, ${it.result?.documents}")
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