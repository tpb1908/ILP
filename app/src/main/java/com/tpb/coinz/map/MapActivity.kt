package com.tpb.coinz.map

import android.graphics.*
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.android.core.location.LocationEngineListener
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode
import com.tpb.coinz.R
import kotlinx.android.synthetic.main.activity_map.*
import com.tpb.coinz.LocationUtils
import com.tpb.coinz.LocationListener
import com.tpb.coinz.asCameraUpdate


class MapActivity : AppCompatActivity(), PermissionsListener {

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

            LocationListener.addListener(object: LocationEngineListener {
                override fun onLocationChanged(location: Location?) {
                    Log.i("LocationEngine", "Location update $location")
                }

                override fun onConnected() {
                    Log.i("LocationEngine", "Location engine connected")
                }
            })

            mapview.getMapAsync {
                locationLayer = LocationLayerPlugin(mapview, it, LocationListener.getEngine())
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
            it.animateCamera(LocationListener.lastLocation().asCameraUpdate())
        }
    }

    private val coinLocationOnClick: View.OnClickListener = View.OnClickListener {
        moveToInitialLocation()
    }

    private fun moveToInitialLocation() {
        mapview.getMapAsync {
            it.animateCamera(CameraUpdateFactory.newLatLngBounds(LocationUtils.bounds, 10))
            it.addPolygon(LocationUtils.polygon
                    .strokeColor(Color.RED)
                    .fillColor(Color.TRANSPARENT))
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