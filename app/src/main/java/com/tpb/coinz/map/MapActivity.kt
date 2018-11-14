package com.tpb.coinz.map

import android.graphics.*
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode
import com.tpb.coinz.*
import com.tpb.coinz.data.location.LocationProvider
import kotlinx.android.synthetic.main.activity_map.*
import java.lang.Exception
import javax.inject.Inject


class MapActivity : AppCompatActivity(), PermissionsListener, MapNavigator {

    @Inject
    lateinit var locationProvider: LocationProvider

    private lateinit var locationLayer: LocationLayerPlugin
    private lateinit var permissionsManager: PermissionsManager

    private lateinit var vm: MapViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        (application as App).mapComponent.inject(this)
        mapview.onCreate(savedInstanceState)
        bindViewModel()

        initLocationSystem()
        if (intent.hasExtra(getString(R.string.extra_camera_position))) {
            val position = intent.getParcelableExtra<CameraPosition>(getString(R.string.extra_camera_position))
            Log.i("MapActivity", "Moving to initial camera position $position")
            setCameraPosition(position)
        } else {
            moveToInitialLocation()
        }

    }

    private fun bindViewModel() {
        vm = ViewModelProviders.of(this).get(MapViewModel::class.java)
        vm.setNavigator(this)
        vm.init()
        vm.coins.observeForever { markers ->
            mapview.getMapAsync {
                it.addMarkers(markers)
            }
        }
    }

    private fun initLocationSystem() {
        locationProvider.start()
        permissionsManager = PermissionsManager(this)
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            fab_coin_area.setOnClickListener(coinLocationOnClick)
            fab_my_location.setOnClickListener(myLocationOnClick)


            mapview.getMapAsync {
                locationLayer = LocationLayerPlugin(mapview, it, LocationListener.getEngine())
                locationLayer.renderMode = RenderMode.COMPASS

                lifecycle.addObserver(locationLayer)

            }
            locationProvider.addListener(object: com.tpb.coinz.data.location.LocationListener {
                override fun locationUpdate(location: Location) {
                    Log.i("MapActivityLocation", "Forcing location update to  " + location)
                    locationLayer.forceLocationUpdate(location)
                }

                override fun locationAvailable() {
                }

                override fun locationUnavailable() {
                }

                override fun locationUpdateError(exception: Exception) {
                }
            })
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

    private val myLocationOnClick: View.OnClickListener = View.OnClickListener {
        //TODO: First click for location, second for zoom
        mapview.getMapAsync {map ->
            locationProvider.lastLocationUpdate()?.apply {
                map.animateCamera(this.asCameraUpdate())
            }
        }
    }

    private val coinLocationOnClick: View.OnClickListener = View.OnClickListener {
        moveToInitialLocation()
    }

    private fun setCameraPosition(position: CameraPosition) {
        mapview.getMapAsync {
            it.cameraPosition = position
        }
    }

    override fun addMarkers(markers: List<MarkerOptions>, callback: (List<Marker>) -> Unit) {
        mapview.getMapAsync {
            callback(it.addMarkers(markers))
        }
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
        locationProvider.start()
    }

    override fun onStart() {
        super.onStart()
        mapview.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapview.onStop()
        locationProvider.stop()
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