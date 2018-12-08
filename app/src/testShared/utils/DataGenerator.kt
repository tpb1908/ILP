package utils

import com.mapbox.mapboxsdk.geometry.LatLng
import com.tpb.coinz.data.coin.Coin
import com.tpb.coinz.data.coin.Currency
import com.tpb.coinz.data.coin.Map
import com.tpb.coinz.data.users.User
import java.util.*
import kotlin.random.Random

object DataGenerator {

    private var id: Int = 0

    fun generateCoin(cid: Int = id++,
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

    fun generateUser(uid: String = (id++).toString(), email: String = "test${id++}@test.com") = User(uid, email)
}