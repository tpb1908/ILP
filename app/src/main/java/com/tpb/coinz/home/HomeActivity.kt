package com.tpb.coinz.home

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseUser
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
import com.tpb.coinz.BuildConfig
import com.tpb.coinz.LocationUtils
import com.tpb.coinz.R
import com.tpb.coinz.data.coins.Coin
import com.tpb.coinz.data.location.LocationListeningEngine
import com.tpb.coinz.data.location.LocationProvider
import com.tpb.coinz.map.MapActivity
import com.tpb.coinz.map.Utils
import kotlinx.android.synthetic.main.activity_home.*
import timber.log.Timber
import javax.inject.Inject

class HomeActivity : AppCompatActivity(), PermissionsListener {

    private val rcLogin = 5534
    private val rcMap = 6543
    private lateinit var vm: HomeViewModel

    @Inject
    lateinit var locationProvider: LocationProvider
    private lateinit var permissionsManager: PermissionsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        (application as App).homeComponent.inject(this)
        home_minimap.onCreate(savedInstanceState)

        initViews(savedInstanceState)
        bindViewModel()
        //startActivity(Intent(this, ThreadsActivity::class.java))
    }

    private fun initViews(savedInstanceState: Bundle?) {
        home_minimap.onCreate(savedInstanceState)
        home_minimap.getMapAsync {
            it.animateCamera(CameraUpdateFactory.newLatLngBounds(LocationUtils.bounds, 10))
            it.addOnMapClickListener { latLng ->
                val cameraPosition = it.cameraPosition
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                        home_minimap,
                        ViewCompat.getTransitionName(home_minimap)!!
                )
                val intent = Intent(this, MapActivity::class.java)
                intent.putExtra(getString(R.string.extra_camera_position), cameraPosition)
                startActivityForResult(intent, rcMap, options.toBundle())
            }
        }
        initLocationSystem()
    }

    private fun initLocationSystem() {
        locationProvider.start()
        permissionsManager = PermissionsManager(this)
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            home_minimap.getMapAsync {
                val engine = LocationListeningEngine(locationProvider)
                val locationLayer = LocationLayerPlugin(home_minimap, it, engine)
                locationLayer.renderMode = RenderMode.COMPASS
                engine.activate()
                lifecycle.addObserver(locationLayer)
            }

        } else {
            permissionsManager.requestLocationPermissions(this)
        }
    }

    private fun bindViewModel() {
        vm = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        (application as App).homeComponent.inject(vm)
        vm.bind()

        vm.user.observe(this, userObserver)
        vm.collectionInfo.observe(this, collectionObserver)
        vm.coins.observe(this, Observer<List<Coin>> { coins ->
            home_minimap.getMapAsync {
                vm.mapMarkers(coins.zip(it.addMarkers(coins.map(::coinToMarkerOption))).toMap().toMutableMap())
            }
        })
        vm.actions.observe(this, Observer {
            when (it) {
                HomeViewModel.HomeActions.BEGIN_LOGIN_FLOW -> beginLoginFlow()
            }
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
        return IconFactory.getInstance(this).fromBitmap(bitmap)
    }

    private val userObserver = Observer<FirebaseUser> {
        Timber.i("User updated $it")
        user_email.text = it.email
    }

    private val collectionObserver = Observer<MapInfo> {
        map_collection_info.text =
                resources.getQuantityString(R.plurals.home_coin_collection_info, it.numCollected, it.numCollected, it.numRemaining)
    }

    private fun beginLoginFlow() {
        val providers = listOf(
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.PhoneBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build())
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(!BuildConfig.DEBUG, true)
                        .setAvailableProviders(providers)
                        .build(),
                rcLogin)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == rcLogin) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                vm.userLoggedIn()

            } else {
                vm.userLoginFailed()
            }
        } else if (requestCode == rcMap) {
            if (data?.hasExtra(getString(R.string.extra_camera_position)) == true) {
                val position = data.getParcelableExtra<CameraPosition>(getString(R.string.extra_camera_position))
                home_minimap.getMapAsync {
                    it.cameraPosition = position
                }
            }
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
        home_minimap.onResume()
        locationProvider.start()
    }

    override fun onStart() {
        super.onStart()
        home_minimap.onStart()
    }

    override fun onStop() {
        super.onStop()
        home_minimap.onStop()
        locationProvider.stop()
    }

    public override fun onPause() {
        super.onPause()
        home_minimap.onPause()
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        home_minimap.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        home_minimap.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        home_minimap.onDestroy()
    }
}