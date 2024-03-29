package utils

import com.mapbox.mapboxsdk.geometry.LatLng
import com.tpb.coinz.data.chat.Message
import com.tpb.coinz.data.chat.Thread
import com.tpb.coinz.data.coin.Coin
import com.tpb.coinz.data.coin.Currency
import com.tpb.coinz.data.coin.Map
import com.tpb.coinz.data.users.User
import java.util.*
import kotlin.random.Random

object DataGenerator {

    private var counter: Int = 0
        get() {
            return ++field
        }
    
    fun generateCoin(cid: Int = counter,
                     value: Double = Random.nextDouble(),
                     currency: Currency = Currency.values().random(),
                     markerSymbol: Int = Currency.values().indexOf(currency),
                     markerColor: Int = Random.nextInt(),
                     location: LatLng = LatLng(
                        Random.nextDouble(55.942617, 55.946233),
                        Random.nextDouble(-3.192473, -3.184319 )
                ),
                     received: Boolean = false) =
            Coin(cid.toString(), value, currency, markerSymbol, markerColor, location, received)

    fun generateCoins(count: Int) = (0..count).map { generateCoin() }

    fun generateMap(dateGenerated: Calendar = Calendar.getInstance(),
                    rates: kotlin.collections.Map<Currency, Double> = Currency.values().associate { Pair(it, Random.nextDouble()) },
                    remainingCoins: MutableList<Coin> = mutableListOf(),
                    collectedCoins: MutableList<Coin> = mutableListOf()) =
            Map(dateGenerated, rates, remainingCoins, collectedCoins)

    fun generateUser(uid: String = (counter).toString(), email: String = "test$counter@test.com") = User(uid, email)

    fun generateMessage(timestamp: Long = System.currentTimeMillis(),
                        sender: User = generateUser(),
                        message: String = "message #$counter",
                        coin: Coin? = null) = Message(timestamp, sender, message, coin)

    fun generateThread(threadId: String = "thread_$counter",
                       creator: User = generateUser(),
                       partner: User = generateUser(),
                       updated: Long = System.currentTimeMillis()) = Thread(threadId, creator, partner, updated)


}