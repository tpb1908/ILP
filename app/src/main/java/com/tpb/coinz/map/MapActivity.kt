package com.tpb.coinz.map

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.annotations.Icon
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode
import com.tpb.coinz.App
import com.tpb.coinz.LocationUtils
import com.tpb.coinz.R
import com.tpb.coinz.asCameraUpdate
import com.tpb.coinz.data.ConnectionLiveData
import com.tpb.coinz.data.coins.Coin
import com.tpb.coinz.data.location.LocationListeningEngine
import com.tpb.coinz.data.location.LocationProvider
import kotlinx.android.synthetic.main.activity_map.*
import timber.log.Timber
import javax.inject.Inject


class MapActivity : AppCompatActivity(), PermissionsListener {

    @Inject lateinit var locationProvider: LocationProvider

    @Inject lateinit var connection: ConnectionLiveData

    private lateinit var locationLayer: LocationLayerPlugin
    private lateinit var permissionsManager: PermissionsManager

    private lateinit var iconFactory: IconFactory

    private lateinit var vm: MapViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        (application as App).mapComponent.inject(this)
        mapview.onCreate(savedInstanceState)
        iconFactory = IconFactory.getInstance(this)
        bindViewModel()

        initLocationSystem()
        if (intent.hasExtra(getString(R.string.extra_camera_position))) {
            val position = intent.getParcelableExtra<CameraPosition>(getString(R.string.extra_camera_position))
            setCameraPosition(position)
        } else {
            moveToInitialLocation()
        }
    }

    private fun bindViewModel() {
        vm = ViewModelProviders.of(this).get(MapViewModel::class.java)
        (application as App).mapComponent.inject(vm)
        vm.bind()
        vm.coins.observe(this, Observer<List<Coin>> { coins ->
            mapview.getMapAsync {
                vm.mapMarkers(coins.zip(it.addMarkers(coins.map(::coinToMarkerOption))).toMap().toMutableMap())
            }
        })
        vm.actions.observe(this, Observer {
            if (it is MapViewModel.MapActions.RemoveMarker) {
                mapview.getMapAsync { map ->
                    map.removeMarker(it.marker)
                }
            } else if (it is MapViewModel.MapActions.DisplayMessage) {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
            }
        })
        connection.observe(this, Observer<Boolean> {

        })

    }

    private fun coinToMarkerOption(coin: Coin): MarkerOptions {
        return MarkerOptions()
                .position(coin.location)
                .title(coin.currency.name)
                .snippet(coin.value.toString())
                .setIcon(getCoinIcon(coin))
    }

    /**
     * Load a marker icon as a tinted [Bitmap] and pass to [IconFactory.fromBitmap] which wraps
     * it to be displayed on the map
     */
    private fun getCoinIcon(coin: Coin): Icon {
        val bitmap = Utils.loadAndTintBitMap(this, R.drawable.ic_location_white_24dp, coin.markerColor)
        return iconFactory.fromBitmap(bitmap)
    }

    private fun initLocationSystem() {
        locationProvider.start()
        permissionsManager = PermissionsManager(this)
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            fab_coin_area.setOnClickListener(coinLocationOnClick)
            fab_my_location.setOnClickListener(myLocationOnClick)


            mapview.getMapAsync {
                val engine = LocationListeningEngine(locationProvider)
                locationLayer = LocationLayerPlugin(mapview, it, engine)
                locationLayer.renderMode = RenderMode.COMPASS
                engine.activate()
                lifecycle.addObserver(locationLayer)
            }

        } else {
            permissionsManager.requestLocationPermissions(this)
        }
    }



    private val coinLocationOnClick: View.OnClickListener = View.OnClickListener {
        moveToInitialLocation()
    }

    private val myLocationOnClick: View.OnClickListener = View.OnClickListener {
        //TODO: First click for location, second for zoom
        mapview.getMapAsync {map ->
            locationProvider.lastLocationUpdate()?.apply {
                map.animateCamera(this.asCameraUpdate())
            }
        }
    }

    private fun setCameraPosition(position: CameraPosition)  = mapview.getMapAsync {
        it.animateCamera(CameraUpdateFactory.newCameraPosition(position))
    }

    private fun moveToInitialLocation() {
        mapview.getMapAsync {
            it.animateCamera(CameraUpdateFactory.newLatLngBounds(LocationUtils.bounds, 10))
            it.addPolygon(LocationUtils.polygon
                    .strokeColor(Color.RED)
                    .fillColor(Color.TRANSPARENT))
        }
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