package com.tpb.coinz.data.coin

import com.nhaarman.mockitokotlin2.*
import com.tpb.coinz.data.coin.collection.CoinCollector
import com.tpb.coinz.data.coin.loading.MapLoader
import com.tpb.coinz.data.coin.storage.MapStore
import com.tpb.coinz.data.config.ConstantConfigProvider
import com.tpb.coinz.data.location.LocationProvider
import org.junit.Test

import org.junit.Before
import utils.DataGenerator

class CoinCollectorImplTest {

    private lateinit var collector: CoinCollectorImpl

    companion object {
        private val mockLocationProvider: LocationProvider = mock()
        private val mockMapLoader: MapLoader = mock()
        private val mockMapStore: MapStore = mock()
        private val map = DataGenerator.generateMap()
    }

    @Before
    fun setUp() {
        reset(mockLocationProvider, mockMapLoader, mockMapStore)
        collector = CoinCollectorImpl(
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
        val mockListener = mock<CoinCollector.CoinCollectorListener>()
        collector.addCollectionListener(mockListener)
        collector.loadMap()

        val captor = argumentCaptor<(Result<Map>) -> Unit>()

        verify(mockMapStore, times(1)).getLatest(captor.capture())

        captor.lastValue.invoke(Result.success(map))
        verify(mockListener, times(1)).mapLoaded(map)
    }

    @Test
    fun testLoadFromNetwork() {
        collector.loadMap()

        val captor = argumentCaptor<(Result<Map>) -> Unit>()
        verify(mockMapStore, times(1)).getLatest(captor.capture())

        captor.lastValue.invoke(Result.failure(Exception()))
        verify(mockMapLoader, times(1)).loadCoins(any(), any())
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