package com.tpb.coinz

import org.mockito.ArgumentCaptor
import org.mockito.Mockito

public inline fun<reified T : Any> anyFunction() = Mockito.mock(T::class.java)

public inline fun <reified T: Any> argumentCaptor() = ArgumentCaptor.forClass(T::class.java)

//https://medium.com/@elye.project/befriending-kotlin-and-mockito-1c2e7b0ef791
public inline fun<T> nonNullAny(): T {
    Mockito.any<T>()
    return uninitialized()
}

fun <T> uninitialized(): T = null as T