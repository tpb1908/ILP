package com.tpb.coinz.map

import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.annotations.PolygonOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.tpb.coinz.LocationListener
import com.tpb.coinz.R
import kotlinx.android.synthetic.main.activity_map.*

class MapActivity : AppCompatActivity() {
    private val viewModel: MapViewModel by lazy {
        ViewModelProviders.of(this).get(MapViewModel::class.java)
    }

    private lateinit var locationListener: LocationListener
    private val markerOptions = MarkerOptions().position(LatLng(0.0, 0.0))
    private var marker: Marker = markerOptions.marker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, "pk.eyJ1IjoidHBiMTkwOCIsImEiOiJjam1vd25pZm0xNWQzM3ZvZWtpZ3hmdmQ5In0.YMMSu09MMG3QPZ4m6_zndQ")
        setContentView(R.layout.activity_map)

        mapview.onCreate(savedInstanceState)

        locationListener = LocationListener(this, updateMapLocationMarker)
        lifecycle.addObserver(locationListener)


        val bounds = LatLngBounds.Builder().include(LatLng(55.946233, -3.192473))
                .include(LatLng(55.946233, -3.184319))
                .include(LatLng(55.942617, -3.192473))
                .include(LatLng(55.942617, -3.184319)).build()

        mapview.getMapAsync {
            it.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10))
            val p = PolygonOptions()
                    .add(LatLng(55.946233, -3.192473))
                    .add(LatLng(55.946233, -3.184319))
                    .add(LatLng(55.942617, -3.184319))
                    .add(LatLng(55.942617, -3.192473))
                    .add(LatLng(55.946233, -3.192473))
                    .strokeColor(Color.RED)
                    .fillColor(Color.TRANSPARENT)
            it.addPolygon(p)
        }
    }

    private val updateMapLocationMarker: (Location) -> Unit = { location ->
        mapview.getMapAsync {
            Log.i("Location callback", "Adding marker from $location")
            val pos = LatLng(location)
            if (it.markers.contains(marker)) {
                marker.position = pos
                it.updateMarker(marker)
            } else {
                markerOptions.position = pos
                marker = it.addMarker(markerOptions)
            }
        }
    }

    public override fun onResume() {
        super.onResume()
        mapview.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapview.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapview.onStop()
    }

    public override fun onPause() {
        super.onPause()
        mapview.onPause()
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapview.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapview.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapview.onDestroy()
    }



}