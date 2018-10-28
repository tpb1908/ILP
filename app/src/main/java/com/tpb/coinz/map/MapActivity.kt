package com.tpb.coinz.map

import android.content.Context
import android.graphics.*
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineListener
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.annotations.PolygonOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode
import com.tpb.coinz.R
import kotlinx.android.synthetic.main.activity_map.*
import com.firebase.ui.auth.AuthUI


class MapActivity : AppCompatActivity(), PermissionsListener {

    private lateinit var locationEngine: LocationEngine
    private lateinit var locationLayer: LocationLayerPlugin
    private lateinit var permissionsManager: PermissionsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        mapview.onCreate(savedInstanceState)
        moveToInitialLocation()

        initLocationSystem()


    }

    private fun initLocationSystem() {
        permissionsManager = PermissionsManager(this)
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
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
                locationLayer.renderMode = RenderMode.GPS
                lifecycle.addObserver(locationLayer)
            }
        } else {
            permissionsManager.requestLocationPermissions(this)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        Log.i(MapActivity::class.java.name, "Permissions to explain $permissionsToExplain")
    }

    override fun onPermissionResult(granted: Boolean) {
        Log.i(MapActivity::class.java.name, "Permission result $granted")
        initLocationSystem()
    }

    private val myLocationOnClick: View.OnClickListener = View.OnClickListener { _ ->
        //TODO: First click for location, second for zoom
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
        mapview.getMapAsync {
            val markerOptions = MarkerOptions()
            markerOptions.position = LatLng(55.9456215,-3.1631175)
            markerOptions.title = "5"
            markerOptions.snippet = "PENY"
            val bitmap = Utils.loadAndTintBitMap(this@MapActivity, R.drawable.ic_location_white_24dp, Color.BLUE)

            markerOptions.icon = IconFactory.getInstance(this@MapActivity).fromBitmap(bitmap)
            it.addMarker(markerOptions)
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