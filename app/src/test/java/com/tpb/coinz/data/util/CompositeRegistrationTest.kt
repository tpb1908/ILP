package com.tpb.coinz.data.util

import org.junit.Test

import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

class CompositeRegistrationTest {

    private val registrations = (1..10).map { Mockito.mock(Registration::class.java) }

    @Test
    fun add() {
        val composite = CompositeRegistration()
        registrations.forEach {
            composite.add(it)
        }
        composite.deregister()
        registrations.forEach {
            verify(it, times(1)).deregister()
        }
    }

}