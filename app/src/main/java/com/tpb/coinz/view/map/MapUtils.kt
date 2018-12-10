package com.tpb.coinz.view.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.location.Location
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.mapbox.mapboxsdk.annotations.Icon
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraUpdate
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.tpb.coinz.R
import com.tpb.coinz.data.coin.Coin

object MapUtils {

    /**
     * Converts a [Coin] to a [MarkerOptions] instance
     */
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

    /**
     * Loads a drawable resource and applies a colour filter to it by drawing it to a canvas.
     * @return A [Bitmap] tinted to [color]
     */
    private fun loadAndTintBitMap(context: Context, @DrawableRes drawableId: Int, @ColorInt color: Int): Bitmap {
        val drawable = ContextCompat.getDrawable(context, drawableId)

        val bitmap = Bitmap.createBitmap(drawable!!.intrinsicWidth,
                drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
        drawable.draw(canvas)

        return bitmap
    }

}

public fun Location.asCameraUpdate(): CameraUpdate = CameraUpdateFactory.newLatLngZoom(LatLng(this), 15.0)
