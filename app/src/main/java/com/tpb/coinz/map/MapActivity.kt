package com.tpb.coinz.map

import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineListener
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.annotations.PolygonOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode
import com.tpb.coinz.LocationListener
import com.tpb.coinz.R
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.activity_map.view.*

class MapActivity : AppCompatActivity() {

    private lateinit var locationEngine: LocationEngine
    private lateinit var locationLayer: LocationLayerPlugin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, "pk.eyJ1IjoidHBiMTkwOCIsImEiOiJjam1vd25pZm0xNWQzM3ZvZWtpZ3hmdmQ5In0.YMMSu09MMG3QPZ4m6_zndQ")
        setContentView(R.layout.activity_map)

        mapview.onCreate(savedInstanceState)


        moveToInitialLocation()
        fab_coin_area.setOnClickListener(coinLocationOnClick)
        fab_my_location.setOnClickListener(myLocationOnClick)

        locationEngine = LocationEngineProvider(applicationContext).obtainBestLocationEngineAvailable()
        locationEngine.activate()
        locationEngine.addLocationEngineListener(object: LocationEngineListener {
            override fun onLocationChanged(location: Location?) {
                Log.i("LocationEngine", "Location update $location")
            }

            override fun onConnected() {
                Log.i("LocationEngine", "Location engine connected")
            }
        })

        mapview.getMapAsync {
            locationLayer = LocationLayerPlugin(mapview, it, locationEngine)
            locationLayer.renderMode = RenderMode.COMPASS
            lifecycle.addObserver(locationLayer)
        }

    }

    private fun checkLocation() {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            //TODO
        }
    }

    private val myLocationOnClick: View.OnClickListener = View.OnClickListener {_ ->
        mapview.getMapAsync {
            it.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(locationEngine.lastLocation), 15.0))
        }
    }

    private val coinLocationOnClick: View.OnClickListener = View.OnClickListener {
        moveToInitialLocation()
    }

    private fun moveToInitialLocation() {
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