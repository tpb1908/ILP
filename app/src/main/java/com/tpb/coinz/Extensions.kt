package com.tpb.coinz

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.location.Location
import android.text.Editable
import android.text.TextWatcher
import androidx.core.content.ContextCompat
import com.mapbox.mapboxsdk.annotations.Icon
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraUpdate
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.tpb.coinz.data.coin.Coin

fun Location.asCameraUpdate(): CameraUpdate = CameraUpdateFactory.newLatLngZoom(LatLng(this), 15.0)

inline fun<T> Result<out T>.ifElse() {

}

inline fun <R> R?.orElse(block: () -> R): R {
    return this ?: block()
}

fun coinToMarkerOption(context: Context, coin: Coin): MarkerOptions {
    return MarkerOptions()
            .position(coin.location)
            .title(coin.currency.name)
            .snippet(coin.value.toString())
            .setIcon(getCoinIcon(context, coin))
}

/**
 * Load a marker icon as a tinted [Bitmap] and pass to [IconFactory.fromBitmap] which wraps
 * it to be displayed on the map
 */
private fun getCoinIcon(context: Context, coin: Coin): Icon {
    val bitmap = loadAndTintBitMap(context, R.drawable.ic_location_white_24dp, coin.markerColor)
    return IconFactory.getInstance(context).fromBitmap(bitmap)
}

private fun loadAndTintBitMap(context: Context, drawableId: Int, color: Int): Bitmap {
    val drawable = ContextCompat.getDrawable(context, drawableId)

    val bitmap = Bitmap.createBitmap(drawable!!.intrinsicWidth,
            drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
    drawable.draw(canvas)

    return bitmap
}

abstract class SimpleTextWatcher : TextWatcher {

    override fun afterTextChanged(p0: Editable?) {
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        p0?.let { onTextChanged(it.toString()) }
    }

    abstract fun onTextChanged(text: String)
}

