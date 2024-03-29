package com.tpb.coinz.data.location

import com.mapbox.android.core.location.LocationEngine
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.junit.Assert.*
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`

class LocationListeningEngineTest {


    private lateinit var lle: LocationListeningEngine

    companion object {
        lateinit var mockLocationProvider: LocationProvider
        private val listeners = mutableListOf<LocationListener>()
        @BeforeClass @JvmStatic
        fun beforeClass() {
            mockLocationProvider = Mockito.mock(LocationProvider::class.java)
            `when`(mockLocationProvider.addListener(any())).then {
                listeners.add(it.getArgument(0))
            }
            `when`(mockLocationProvider.removeListener(any())).then {
                listeners.remove(it.getArgument(0))
            }
        }
    }


    @Before
    fun setUp() {
        listeners.clear()
        lle = LocationListeningEngine(mockLocationProvider)
    }

    @Test
    fun activate() {
        lle.activate()
        assertTrue(listeners.contains(lle))
    }

    @Test
    fun deactivate() {
        lle.activate()
        lle.deactivate()
        assertFalse(listeners.contains(lle))
    }

    /**
     * Test that [LocationListeningEngine] requests that [LocationProvider] begin issuing location updates when
     * [LocationListeningEngine.removeLocationUpdates] is called
     */
    @Test
    fun requestLocationUpdates() {
        lle.requestLocationUpdates()
        verify(mockLocationProvider, times(1)).start()
    }

    /**
     * Test that [LocationListeningEngine] stops requesting updates when [LocationListeningEngine.removeLocationUpdates]
     * is called
     */
    @Test
    fun removeLocationUpdates() {
        lle.removeLocationUpdates()
        verify(mockLocationProvider, times(1)).removeListener(lle)
    }

    /**
     * Test that [LocationListeningEngine] connection state corresponds to locationAvailable/Unavailable calls from
     * [LocationProvider]
     */
    @Test
    fun isConnected() {
        lle.locationAvailable()
        assertTrue(lle.isConnected)
        lle.locationUnavailable()
        assertFalse(lle.isConnected)
    }

    @Test
    fun obtainType() {
        assertEquals(LocationEngine.Type.GOOGLE_PLAY_SERVICES, lle.obtainType())
    }

    /**
     * Test that [LocationListeningEngine] retrieves the last location from its [LocationProvider]
     */
    @Test
    fun getLastLocation() {
        lle.lastLocation
        verify(mockLocationProvider, times(1)).lastLocationUpdate()
    }


}