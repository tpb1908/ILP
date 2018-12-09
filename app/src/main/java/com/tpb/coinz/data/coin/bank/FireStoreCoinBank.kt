package com.tpb.coinz.data.coin.bank

import android.content.SharedPreferences
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.tpb.coinz.data.coin.Coin
import com.tpb.coinz.data.coin.Currency
import com.tpb.coinz.data.coin.FireStoreCoinManager
import com.tpb.coinz.data.coin.Transaction
import com.tpb.coinz.data.config.ConfigProvider
import com.tpb.coinz.data.users.User
import com.tpb.coinz.data.util.Conversion
import com.tpb.coinz.data.util.Conversion.fromMap
import com.tpb.coinz.data.util.FireStoreRegistration
import com.tpb.coinz.data.util.Registration
import timber.log.Timber
import java.util.*

class FireStoreCoinBank(private val prefs: SharedPreferences, store: FirebaseFirestore, val config: ConfigProvider) : FireStoreCoinManager(store), CoinBank {

    private inline fun banked(user: User): CollectionReference = store.collection(collected).document(user.uid).collection(banked)

    private val maxBankable = config.dailyBankLimit

    private var numBankable = 0
        set(value) {
            field = value
            Timber.i("Updating numBankable to $value")
            prefs.edit().putInt("num_bankable", value).apply()
        }

    private var bankDay: Calendar = Calendar.getInstance()
        set(value) {
            field = value
            prefs.edit().putLong("bank_day", value.timeInMillis).apply()
        }

    init {
        if (prefs.contains("bank_day")) {
            val day = prefs.getLong("bank_day", -1)
            val cal = Calendar.getInstance()
            cal.timeInMillis = day
            bankDay = cal
            numBankable = prefs.getInt("num_bankable", 0)
            checkCurrentDay()
            Timber.i("Loaded numBankable from prefs as $numBankable")
        } else { // First run
            Timber.i("First run of FireStoreCoinBank")
            bankDay = Calendar.getInstance()
            numBankable = maxBankable
        }
    }

    override fun bankCoins(user: User, coins: List<Coin>, rates: Map<Currency, Double>, callback: (Result<List<Coin>>) -> Unit) {
        if (coins.count {!it.received} <= numBankable) {
            val successfullyBanked = mutableListOf<Coin>()
            var callCompleteCount = 0

            coins.forEach { coin ->
                // We have to check that received as well as id to stop banking of a collected coin when we
                // actually want to bank a received coin
                coins(user).whereEqualTo("id", coin.id).whereEqualTo("received", coin.received).get()
                        .addOnCompleteListener { getTask ->
                            if (getTask.isSuccessful) {
                                // The query could return more than one document if the user has been sent the same coin twice
                                // In this case it doesn't matter which one of the coins we bank
                                getTask.result?.documents?.first()?.let { ds ->
                                    val map = Conversion.toMap(coin)
                                    map["bank_time"] = System.currentTimeMillis()
                                    map["banked_value"] = coin.value * rates[coin.currency]!!
                                    //TODO: We have to make this change before attempting to update the banked values
                                    // otherwise their listeners will be called before numBankable changes
                                    numBankable -= 1
                                    banked(user).add(map).addOnCompleteListener { addTask ->
                                        if (addTask.isSuccessful) {
                                            ds.reference.delete()
                                            successfullyBanked.add(coin)
                                        } else {
                                            Timber.e(addTask.exception, "Failed to bank coin $coin")
                                        }
                                        callCompleteCount++
                                        if (callCompleteCount == coins.size) {
                                            numBankable += coins.count { !it.received } - successfullyBanked.count { !it.received }
                                            callback(Result.success(successfullyBanked))
                                        }
                                    }
                                }
                            } else {
                                Timber.e(getTask.exception, "Failed to get coin $coin")
                                callCompleteCount++
                                if (callCompleteCount == coins.size) {
                                    numBankable += coins.count { !it.received } - successfullyBanked.count { !it.received }
                                    callback(Result.success(successfullyBanked))
                                }
                            }
                        }
            }
        } else {
            callback(Result.failure(Exception())) // TODO Proper error
        }
    }

    override fun getBankableCoins(user: User, listener: (Result<List<Coin>>) -> Unit) =
            FireStoreRegistration(coins(user).addSnapshotListener { qs, exception ->
                if (qs != null) {
                    val coins = mutableListOf<Coin>()
                    qs.documents.forEach { ds ->
                        ds.data?.let { coins.add(fromMap(it)) }
                    }
                    Timber.i("Coins $coins")
                    listener(Result.success(coins))
                } else {
                    Timber.e(exception, "Query unsuccessful")
                    listener(Result.failure(Conversion.convertFireStoreException(exception)))
                }
            })

    override fun getBankedCoins(user: User, listener: (Result<List<Transaction>>) -> Unit): Registration =
            FireStoreRegistration(banked(user).addSnapshotListener { qs, exception ->
                convertQuerySnapshot(qs, exception, listener)
            })

    override fun getRecentlyBankedCoins(user: User, count: Int, listener: (Result<List<Transaction>>) -> Unit): Registration =
            FireStoreRegistration(banked(user).orderBy("bank_time").limit(count.toLong()).addSnapshotListener { qs, exception ->
                convertQuerySnapshot(qs, exception, listener)
            })

    private fun convertQuerySnapshot(qs: QuerySnapshot?, exception: FirebaseFirestoreException?, listener: (Result<List<Transaction>>) -> Unit) {
        if (qs != null) {
            val transactions = mutableListOf<Transaction>()
            qs.documents.forEach { ds ->
                ds.data?.let {
                    val time = it["bank_time"] as Long
                    val value = it["banked_value"] as Double
                    transactions.add(Transaction(time, value, fromMap(it)))
                }
            }
            Timber.i("Coins $transactions")
            listener(Result.success(transactions))
        } else {
            Timber.e(exception, "Query unsuccessful")
            listener(Result.failure(Conversion.convertFireStoreException(exception)))
        }
    }

    private fun checkCurrentDay() {
        val now = Calendar.getInstance()
        if (now.get(Calendar.DAY_OF_YEAR) != bankDay.get(Calendar.DAY_OF_YEAR)) {
            Timber.i("Current day has changed. Resetting bank limit")
            bankDay = now
            numBankable = maxBankable
        }
    }

    override fun getNumBankable(): Int {
        checkCurrentDay()
        return numBankable
    }

}