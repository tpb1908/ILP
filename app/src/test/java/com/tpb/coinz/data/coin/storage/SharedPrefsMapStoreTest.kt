package com.tpb.coinz.data.coin.storage

import android.content.SharedPreferences
import com.tpb.coinz.anyFunction
import com.tpb.coinz.argumentCaptor
import com.tpb.coinz.data.coin.Currency
import com.tpb.coinz.data.coin.Map
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.BeforeClass
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Mockito.*
import java.util.*

class SharedPrefsMapStoreTest {

    private lateinit var sharedPrefsMapStore: SharedPrefsMapStore

    companion object {

        private val map = hashMapOf<String, String>()
        private val prefs = Mockito.mock(SharedPreferences::class.java)


        @BeforeClass @JvmStatic
        fun beforeClass() {
            val editor = Mockito.mock(SharedPreferences.Editor::class.java)
            `when`(prefs.edit()).thenReturn(editor)
            `when`(prefs.getString(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenAnswer {
                val key = it.getArgument(0) as String
                val default = it.getArgument(1) as String
                return@thenAnswer if (map.containsKey(key)) map[key] else default
            }
            `when`(editor.putString(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenAnswer {
                val key = it.getArgument(0) as String
                val json = it.getArgument(1) as String
                map[key] = json
                return@thenAnswer editor
            }
            `when`(editor.apply()).then {

            }
        }
    }

    @Before
    fun setUp() {
        map.clear()
        sharedPrefsMapStore = SharedPrefsMapStore(prefs)


    }

    @Test
    fun store() {
        val testMap = Map(Calendar.getInstance(), mapOf(), mutableListOf(), mutableListOf())
        sharedPrefsMapStore.store(testMap)
        assertTrue("Mock prefs map should contain map key", map.containsKey("map"))
    }

    @Test
    fun update() {
        val firstMap = Map(Calendar.getInstance(), mapOf(), mutableListOf(), mutableListOf())
        sharedPrefsMapStore.store(firstMap)
        val firstStoredValue = map["map"]
        val updatedMap = firstMap.copy(rates = mapOf(Currency.DOLR to 1.0))
        sharedPrefsMapStore.update(updatedMap)
        val newStoredValue = map["map"]
        assertNotEquals("Stored map JSON should differ", firstStoredValue, newStoredValue)
    }



    @Test
    fun getLatestSuccess() {
        val testMap = Map(Calendar.getInstance(), mapOf(), mutableListOf(), mutableListOf())
        sharedPrefsMapStore.store(testMap)
        val mockCallback = anyFunction<((Result<Map>) -> Unit)>()
        sharedPrefsMapStore.getLatest(mockCallback)
        verify(mockCallback, times(1)).invoke(Result.success(testMap))
    }

    @Test
    fun getLatestFailure() {
        val mockCallback = anyFunction<((Result<Map>) -> Unit)>()
        sharedPrefsMapStore.getLatest(mockCallback)
        val captor = argumentCaptor<Result<Map>>()
        verify(mockCallback, times(1)).invoke(captor.capture())
        assertTrue(captor.value.isFailure)
    }
}