package com.tpb.coinz.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProviders
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode
import com.tpb.coinz.LocationConstants
import com.tpb.coinz.LocationListener
import com.tpb.coinz.R
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity(), HomeNavigator {

    private val RC_LOGIN = 5534
    private lateinit var vm: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initViews(savedInstanceState)
        bindViewModel()
    }

    private fun initViews(savedInstanceState: Bundle?) {
        home_minimap.onCreate(savedInstanceState)
        home_minimap.getMapAsync {
            val locationLayer = LocationLayerPlugin(home_minimap, it, LocationListener.getEngine())
            locationLayer.renderMode = RenderMode.COMPASS
            locationLayer.cameraMode = CameraMode.TRACKING_COMPASS
            lifecycle.addObserver(locationLayer)
            it.animateCamera(CameraUpdateFactory.newLatLngBounds(LocationConstants.bounds, 10))
        }
    }

    private fun bindViewModel() {
        vm = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        vm.setNavigator(this)
        vm.init()
    }

    override fun beginLoginFlow() {
        val providers = listOf(
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.PhoneBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build())
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
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