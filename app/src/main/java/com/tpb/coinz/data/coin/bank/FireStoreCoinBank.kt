package com.tpb.coinz.data.coin.bank

import android.content.SharedPreferences
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.tpb.coinz.Result
import com.tpb.coinz.data.coin.Coin
import com.tpb.coinz.data.coin.FireStoreCoinManager
import com.tpb.coinz.data.users.User
import timber.log.Timber
import java.util.*

class FireStoreCoinBank(private val prefs: SharedPreferences, store: FirebaseFirestore) : FireStoreCoinManager(store), CoinBank {

    private val maxBankable = 25

    private var numBankable = 0
        set(value) {
            field = value
            prefs.edit().putInt("num_bankable", value).apply()
        }

    private var bankDay: Calendar = Calendar.getInstance()
        set(value) {
            field = value
            prefs.edit().putLong("bank_day", value.timeInMillis).apply()
        }

    init {
        if (prefs.contains("day")) {
            val day = prefs.getLong("day", -1)
            val cal = Calendar.getInstance()
            cal.timeInMillis = day
            bankDay = cal
            numBankable = prefs.getInt("num_bankable", 0)
        } else { // First run
            bankDay = Calendar.getInstance()
            numBankable = maxBankable
        }
    }


    override fun bankCoins(user: User, coins: List<Coin>, callback: (Result<List<Coin>>) -> Unit) {
        if (coins.size <= numBankable) {
            val successfullyBanked = mutableListOf<Coin>()
            var successCount = 0
            coins.forEach { coin ->
                // We have to check that received as well as id to stop banking of a collected coin when we
                // actually want to bank a received coin
                coins(user).whereEqualTo("id", coin.id).whereEqualTo("received", coin.received).get()
                        .addOnCompleteListener { getTask ->
                    if (getTask.isSuccessful) {
                        // The query could return more than one document if the user has been sent the same coin twice
                        // In this case it doesn't matter which one of the coins we bank
                        getTask.result?.documents?.first()?.let { ds ->
                            banked(user).add(coin).addOnCompleteListener { addTask ->
                                if (addTask.isSuccessful) {
                                    ds.reference.delete()
                                    successfullyBanked.add(coin)
                                    if (!coin.received) numBankable -= 1
                                } else {
                                    Timber.e(addTask.exception, "Failed to bank coin $coin")
                                    //TODO: Error
                                }
                                successCount++
                                if (successCount == coins.size) callback(Result.Value(successfullyBanked))
                            }
                        }
                    } else {
                        Timber.e(getTask.exception, "Failed to get coin $coin")
                        successCount++
                        if (successCount == coins.size) callback(Result.Value(successfullyBanked))
                        //TODO: Error
                    }
                }
            }
        } else {
            callback(Result.None) //TODO: Proper error
        }
    }

    override fun getBankableCoins(user: User, callback: (Result<List<Coin>>) -> Unit) {
        //TODO: Realtime?
        coins(user).get().addOnCompleteListener { qs ->
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

    private fun checkCurrentDay() {
        val now = Calendar.getInstance()
        if (now.get(Calendar.DAY_OF_YEAR) != bankDay.get(Calendar.DAY_OF_YEAR)) {
            bankDay = now
            numBankable = maxBankable
        }
    }

    override fun getNumBankable(): Int {
        checkCurrentDay()
        return numBankable
    }
}