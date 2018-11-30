package com.tpb.coinz

import android.location.Location
import android.text.Editable
import android.text.TextWatcher
import com.mapbox.mapboxsdk.camera.CameraUpdate
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng

fun Location.asCameraUpdate(): CameraUpdate = CameraUpdateFactory.newLatLngZoom(LatLng(this), 15.0)



sealed class Result<out T> {
    object None: Result<Nothing>()
    data class Value<T>(val v: T) : Result<T>()
}

fun<T> Collection<T>.startsWith(other: Collection<T>): Boolean = this.zip(other).all { it.first?.equals(it.second) ?: false }

abstract class SimpleTextWatcher: TextWatcher {

    override fun afterTextChanged(p0: Editable?) {
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        p0?.let { onTextChanged(it.toString()) }
    }

    abstract fun onTextChanged(text: String)
}

