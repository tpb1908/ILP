package com.tpb.coinz.view

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.tpb.coinz.view.base.ActionLiveData
import org.junit.Before
import org.junit.Rule
import org.junit.Test

import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Mockito.*

class ActionLiveDataTest {

    private lateinit var ld: ActionLiveData<String>
    private val observer =  Mockito.mock(Observer::class.java) as Observer<String>

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        ld = ActionLiveData()
    }

    @Test
    fun testObserverTriggered() {
        val string = "some_string"
        ld.postValue(string)
        ld.observeForever(observer)
        verify(observer, times(1)).onChanged(string)
    }

    @Test
    fun testValueCleared() {
        val string = "some_string"
        ld.postValue(string)
        ld.observeForever {  } // consume the value
        ld.observeForever(observer)
        verify(observer, times(0)).onChanged(ArgumentMatchers.anyString())
    }

}