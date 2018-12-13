package com.tpb.coinz.view.map

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mapbox.mapboxsdk.annotations.Marker
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.tpb.coinz.data.coin.Coin
import com.tpb.coinz.data.coin.collection.CoinCollector
import com.tpb.coinz.data.users.UserCollection
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*
import utils.DataGenerator

class MapViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var vm: MapViewModel

    companion object {
        private val userCollection = mock(UserCollection::class.java)
        private val coinCollector = mock(CoinCollector::class.java)
        private val user = DataGenerator.generateUser()
        private val map = DataGenerator.generateMap(remainingCoins = DataGenerator.generateCoins(10).toMutableList())
    }

    @Before
    fun setUp() {
        vm = MapViewModel(coinCollector)
        reset(coinCollector)

        `when`(userCollection.getCurrentUser()).thenReturn(user)
    }


    @Test
    fun bind() {
        vm.bind()
        verify(coinCollector, times(1)).addCollectionListener(vm)
        verify(coinCollector, times(1)).loadMap()
    }

    /**
     * Test that [MapViewModel] attempts to load the current [Map] and posts the loaded coins to
     * [MapViewModel.coins]
     */
    @Test
    fun mapLoaded() {
        val captor = argumentCaptor<CoinCollector.CoinCollectorListener>()
        vm.bind()
        verify(coinCollector, times(1)).addCollectionListener(captor.capture())
        val coinObserver = mock<Observer<List<Coin>>>()
        val coinCaptor = argumentCaptor<List<Coin>>()
        vm.coins.observeForever(coinObserver)


        captor.lastValue.mapLoaded(map)

        verify(coinObserver, times(1)).onChanged(coinCaptor.capture())
        assertThat(map.remainingCoins, `is`(coinCaptor.lastValue))
    }


    /**
     * Test that the [MapViewModel] posts actions to clear markers and notify the user that the map is being reloaded
     * when the [CoinCollector.CoinCollectorListener.notifyReloading] method is called
     */
    @Test
    fun notifyReloading() {
        val actionObserver = mock<Observer<MapViewModel.MapAction>>()
        val actionCaptor = argumentCaptor<MapViewModel.MapAction>()
        val coinsObserver = mock<Observer<List<Coin>>>()
        val coinCaptor = argumentCaptor<List<Coin>>()

        vm.actions.observeForever(actionObserver)
        vm.coins.observeForever(coinsObserver)
        vm.notifyReloading() // As would be called by coinCollector

        verify(actionObserver, times(1)).onChanged(actionCaptor.capture())
        assertTrue(actionCaptor.lastValue is MapViewModel.MapAction.ClearMarkers)

        verify(coinsObserver, times(1)).onChanged(coinCaptor.capture())
        assertTrue(coinCaptor.lastValue.isEmpty())
    }

    /**
     * Test that the [MapViewModel] posts an action to [MapViewModel.actions] to display a message that all coins
     * have been collected
     */
    @Test
    fun messageOnAllCollected() {
        vm.mapLoaded(map)
        val markers = map.remainingCoins.associate { Pair(it, mock(Marker::class.java)) }.toMutableMap()
        vm.setMapMarkers(markers)
        val actionObserver = mock<Observer<MapViewModel.MapAction>>()
        val actionCaptor = argumentCaptor<MapViewModel.MapAction>()
        vm.actions.observeForever(actionObserver)

        vm.coinsCollected(map.remainingCoins)
        verify(actionObserver, times(map.remainingCoins.size + 1)).onChanged(actionCaptor.capture())
        assertTrue(actionCaptor.allValues.dropLast(1).all { it is MapViewModel.MapAction.RemoveMarker })
        assertTrue(actionCaptor.allValues.last() is MapViewModel.MapAction.DisplayMessage)
    }

}