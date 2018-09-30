package com.tpb.coinz.map

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdate
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.tpb.coinz.R
import kotlinx.android.synthetic.main.activity_map.*

class MapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, "pk.eyJ1IjoidHBiMTkwOCIsImEiOiJjam1vd25pZm0xNWQzM3ZvZWtpZ3hmdmQ5In0.YMMSu09MMG3QPZ4m6_zndQ")
        setContentView(R.layout.activity_map)

        val bounds = LatLngBounds.Builder().include(LatLng(55.946233, -3.192473))
                .include(LatLng(55.946233, -3.184319))
                .include(LatLng(55.942617, -3.192473))
                .include(LatLng(55.942617, -3.184319)).build()

        mapview.getMapAsync {
            it.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10))
        }
    }
}