package com.tpb.coinz.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseUser
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode
import com.tpb.coinz.App
import com.tpb.coinz.BuildConfig
import com.tpb.coinz.LocationUtils
import com.tpb.coinz.R
import com.tpb.coinz.bank.BankActivity
import com.tpb.coinz.map.MapActivity
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity(), HomeNavigator {

    private val RC_LOGIN = 5534
    private lateinit var vm: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        (application as App).homeComponent.inject(this)
        home_minimap.onCreate(savedInstanceState)

        initViews(savedInstanceState)
        bindViewModel()
        startActivity(Intent(this, BankActivity::class.java))
    }

    private fun initViews(savedInstanceState: Bundle?) {
        home_minimap.onCreate(savedInstanceState)
        home_minimap.getMapAsync {
            val locationLayer = LocationLayerPlugin(home_minimap, it, LocationEngineProvider(applicationContext).obtainBestLocationEngineAvailable())
            locationLayer.renderMode = RenderMode.COMPASS
            locationLayer.cameraMode = CameraMode.TRACKING_COMPASS
            lifecycle.addObserver(locationLayer)
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
    }

    private fun bindViewModel() {
        vm = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        vm.setNavigator(this)
        vm.init()

        vm.user.observe(this, userObserver)
        vm.collectionInfo.observe(this, collectionObserver)
    }

    private val userObserver = Observer<FirebaseUser> {
        Log.i("UserObserver", "User updated $it")
        user_email.text = it.email
    }

    private val collectionObserver = Observer<MapInfo> {
        map_collection_info.text = getString(R.string.home_coin_collection_info, it.numCollected, it.numRemaining)
    }

    override fun beginLoginFlow() {
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
                RC_LOGIN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_LOGIN) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                vm.userLoggedIn()
            } else {
                vm.userLoginFailed()
            }
        }
    }


}