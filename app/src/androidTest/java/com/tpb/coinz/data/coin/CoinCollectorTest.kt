package com.tpb.coinz.data.coin

import android.location.Location
import com.tpb.coinz.data.coin.loading.MapLoader
import com.tpb.coinz.data.coin.storage.MapStore
import com.tpb.coinz.data.config.ConstantConfigProvider
import com.tpb.coinz.data.location.LocationListener
import com.tpb.coinz.data.location.LocationProvider
import org.junit.Test

import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class CoinCollectorTest {

    private val mockLocationProvider = object: LocationProvider {
        override fun addListener(listener: LocationListener) {
        }

        override fun removeListener(listener: LocationListener) {
        }

        override fun start() {
        }

        override fun pause() {
        }

        override fun stop() {
        }

        override fun lastLocationUpdate(): Location? {
            return null
        }
    }

    private val mockMapLoader = object : MapLoader {
        override fun loadCoins(date: Calendar, listener: (Result<Map>) -> Unit) {

        }
    }

    private val mockMapStore = Mockito.mock(MapStore::class.java)

    private val emptyMap = Map(Calendar.getInstance(), mapOf(), mutableListOf(), mutableListOf())

    lateinit var collector: CoinCollector

    @Before
    fun initCollector() {
        collector = CoinCollector(
                mockLocationProvider,
                mockMapLoader,
                mockMapStore,
                ConstantConfigProvider
        )
    }


    /**
     * Test that CoinCollectorListener is notified when map is loaded
     */
    @Test
    fun testLoadMapListeners() {
        val mockListener = Mockito.mock(CoinCollector.CoinCollectorListener::class.java)
        collector.addCollectionListener(mockListener)
        collector.loadMap()
        `when`(mockMapStore.getLatest(anyObject())).thenAnswer {

            (it.getArgument(0) as ((Result<Map>) -> Unit)).invoke(
                    Result.success(emptyMap)
            )
            verify(mockListener, times(1)).mapLoaded(emptyMap)
        }
    }

    @Test
    fun testLoadFromNetwork() {
        collector.loadMap()
        `when`(mockMapStore.getLatest(anyObject())).thenAnswer {
            (it.getArgument(0) as ((Result<Map>) -> Unit)).invoke(
                Result.failure(Exception())
            )
            verify(mockMapLoader, times(1)).loadCoins(anyObject(), anyObject())
        }
    }


    @Test
    fun addCollectionListener() {
    }

    @Test
    fun removeCollectionListener() {
    }

    @Test
    fun dispose() {

    }

    @Test
    fun locationUpdate() {

    }
}