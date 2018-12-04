package com.tpb.coinz.view.map

import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.tpb.coinz.*
import com.tpb.coinz.data.ConnectionLiveData
import com.tpb.coinz.data.coin.Coin
import com.tpb.coinz.data.config.ConfigProvider
import com.tpb.coinz.data.location.LocationListener
import com.tpb.coinz.data.location.LocationListeningEngine
import com.tpb.coinz.data.location.LocationProvider
import kotlinx.android.synthetic.main.activity_map.*
import timber.log.Timber
import javax.inject.Inject


class MapActivity : AppCompatActivity(), PermissionsListener {

    @Inject lateinit var locationProvider: LocationProvider

    @Inject lateinit var connection: ConnectionLiveData

    @Inject lateinit var config: ConfigProvider

    private lateinit var permissionsManager: PermissionsManager


    private lateinit var vm: MapViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        (application as App).activityComponent.inject(this)
        mapview.onCreate(savedInstanceState)
        bindViewModel()

        initLocationSystem()
        if (intent.hasExtra(getString(R.string.extra_camera_position))) {
            val position = intent.getParcelableExtra<CameraPosition>(getString(R.string.extra_camera_position))
            setCameraPosition(position)
            intent.removeExtra(getString(R.string.extra_camera_position))
        } else {
            if (savedInstanceState == null) {
                moveToCoinArea()
                moveToUserLocation()
            }
        }

    }

    private fun bindViewModel() {
        vm = ViewModelProviders.of(this).get(MapViewModel::class.java)
        (application as App).mapComponent.inject(vm)
        vm.bind()
        vm.coins.observe(this, Observer<List<Coin>> { coins ->
            mapview.getMapAsync {
                vm.mapMarkers(coins.zip(it.addMarkers(coins.map{coinToMarkerOption(this, it)})).toMap().toMutableMap())
            }
        })
        vm.actions.observe(this, Observer {
            when (it) {
                is MapViewModel.MapAction.RemoveMarker -> mapview.getMapAsync { map ->
                    map.removeMarker(it.marker)
                }
                is MapViewModel.MapAction.DisplayMessage -> Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                is MapViewModel.MapAction.ClearMarkers -> mapview.getMapAsync { map ->
                    map.markers.forEach { map.removeMarker(it) }
                }
            }
        })
        connection.observe(this, Observer<Boolean> {

        })

    }

    private fun initLocationSystem() {
        locationProvider.start()
        permissionsManager = PermissionsManager(this)
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            fab_coin_area.setOnClickListener(coinLocationOnClick)
            fab_my_location.setOnClickListener(myLocationOnClick)

            mapview.getMapAsync {
                val lc = it.locationComponent
                lc.isLocationComponentEnabled = true
                val engine = LocationListeningEngine(locationProvider)
                engine.activate()
                lc.activateLocationComponent(this, engine)
                lc.renderMode = RenderMode.NORMAL
            }
        } else {
            permissionsManager.requestLocationPermissions(this)
        }
    }


    private fun moveToCoinArea() {
        mapview.getMapAsync {
            it.animateCamera(CameraUpdateFactory.newLatLngBounds(config.collectionAreaBounds, 10))
            it.addPolygon(config.collectionAreaPolygon
                    .strokeColor(Color.RED)
                    .fillColor(Color.TRANSPARENT))
        }

    }

    private fun moveToUserLocation() {
        Timber.i("Moving to user location")
        locationProvider.addListener(object: LocationListener.SimpleLocationListener {
            override fun locationUpdate(location: Location) {
                mapview.getMapAsync { it.animateCamera(location.asCameraUpdate()) }
                locationProvider.removeListener(this)
            }
        })
    }

    private val coinLocationOnClick: View.OnClickListener = View.OnClickListener {
        moveToCoinArea()
    }

    private val myLocationOnClick: View.OnClickListener = View.OnClickListener {
        //TODO: First click for location, second for zoom
        mapview.getMapAsync { map ->
            locationProvider.lastLocationUpdate()?.apply {
                map.animateCamera(this.asCameraUpdate())
            }
        }
    }

    private fun setCameraPosition(position: CameraPosition) = mapview.getMapAsync {
        Timber.i("Setting camera position to $position")
        it.animateCamera(CameraUpdateFactory.newCameraPosition(position))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        Timber.i("Permissions to explain $permissionsToExplain")
    }

    override fun onPermissionResult(granted: Boolean) {
        Timber.i("Permission result $granted")
        initLocationSystem()
    }

    override fun onBackPressed() {
        mapview.getMapAsync {
            setResult(Activity.RESULT_OK, Intent().putExtra(getString(R.string.extra_camera_position), it.cameraPosition))
            super.onBackPressed()
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
        locationProvider.pause()
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