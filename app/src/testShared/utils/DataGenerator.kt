package utils

import com.mapbox.mapboxsdk.geometry.LatLng
import com.tpb.coinz.data.coin.Coin
import com.tpb.coinz.data.coin.Currency
import kotlin.random.Random

object DataGenerator {

    private var id: Int = 0

    fun getCoin(cid: Int = id++,
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

}