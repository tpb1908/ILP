package com.tpb.coinz.home

import android.app.Activity
import android.content.Intent
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
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode
import com.tpb.coinz.App
import com.tpb.coinz.BuildConfig
import com.tpb.coinz.LocationUtils
import com.tpb.coinz.R
import com.tpb.coinz.data.location.LocationListeningEngine
import com.tpb.coinz.data.location.LocationProvider
import com.tpb.coinz.map.MapActivity
import com.tpb.coinz.messaging.threads.ThreadsActivity
import kotlinx.android.synthetic.main.activity_home.*
import timber.log.Timber
import javax.inject.Inject

class HomeActivity : AppCompatActivity(), PermissionsListener {

    private val rcLogin = 5534
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
        startActivity(Intent(this, ThreadsActivity::class.java))
    }

    private fun initViews(savedInstanceState: Bundle?) {
        home_minimap.onCreate(savedInstanceState)
        home_minimap.getMapAsync {
            it.animateCamera(CameraUpdateFactory.newLatLngBounds(LocationUtils.bounds, 10))
            it.addOnMapClickListener {latLng ->
                val cameraPosition = it.cameraPosition
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                        home_minimap,
                        ViewCompat.getTransitionName(home_minimap)!!
                )
                val intent = Intent(this, MapActivity::class.java)
                intent.putExtra(getString(R.string.extra_camera_position), cameraPosition)
                startActivity(intent, options.toBundle())
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
        vm.actions.observe(this, Observer {
            when (it) {
                HomeViewModel.HomeActions.BEGIN_LOGIN_FLOW -> beginLoginFlow()
            }
        })
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
        }
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        Timber.i("Permissions to explain $permissionsToExplain")
    }

    override fun onPermissionResult(granted: Boolean) {
        Timber.i("Permission result $granted")
        initLocationSystem()
    }
}