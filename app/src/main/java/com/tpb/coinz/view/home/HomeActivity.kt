package com.tpb.coinz.view.home

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.firebase.ui.auth.data.model.User
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.tpb.coinz.*
import com.tpb.coinz.data.chat.Thread
import com.tpb.coinz.data.config.ConfigProvider
import com.tpb.coinz.data.location.background.GeofenceTransitionsIntentService
import com.tpb.coinz.data.location.LocationListener
import com.tpb.coinz.data.location.LocationListeningEngine
import com.tpb.coinz.data.location.LocationProvider
import com.tpb.coinz.view.bank.BankActivity
import com.tpb.coinz.view.map.MapActivity
import com.tpb.coinz.view.messaging.thread.ThreadActivity
import com.tpb.coinz.view.messaging.threads.ThreadsActivity
import com.tpb.coinz.view.messaging.threads.ThreadsRecyclerAdapter
import kotlinx.android.synthetic.main.activity_home.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

import kotlin.math.max

class HomeActivity : AppCompatActivity(), PermissionsListener {

    private val rcLogin = 5534
    private val rcMap = 6543
    private val vm: HomeViewModel by viewModel()

    val config: ConfigProvider by inject()

    val locationProvider: LocationProvider by inject()
    private lateinit var permissionsManager: PermissionsManager

    private val threadsAdapter = ThreadsRecyclerAdapter()
    private val bankedAdapter = BankedRecyclerAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        initViews(savedInstanceState)
        bindViewModel()
        if (savedInstanceState == null) {
            moveToCoinArea()
            moveToUserLocation()

        }

    }

    private fun initViews(savedInstanceState: Bundle?) {
        home_minimap.onCreate(savedInstanceState)
        home_minimap.getMapAsync {
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
        //https://stackoverflow.com/questions/36692453/using-mapbox-in-scrollview-scrollview-sliding-to-the-position-where-mapbox-is-p
        home_minimap.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_MOVE -> {
                    home_scrollview.requestDisallowInterceptTouchEvent(true)
                }
                MotionEvent.ACTION_CANCEL -> {
                    home_scrollview.requestDisallowInterceptTouchEvent(false)
                }
            }
            home_minimap.onTouchEvent(motionEvent)
        }
        moveToCoinArea()
        initLocationSystem()

        recent_threads_recycler.layoutManager = LinearLayoutManager(this)
        recent_threads_recycler.adapter = threadsAdapter
        threadsAdapter.onClick = this::openThread

        recent_threads_header.setOnClickListener {
            startActivity(Intent(this, ThreadsActivity::class.java))
        }

        recently_banked_recycler.layoutManager = LinearLayoutManager(this)
        recently_banked_recycler.adapter = bankedAdapter

        bank_header.setOnClickListener {
            startActivity(Intent(this, BankActivity::class.java))
        }

    }

    private fun moveToCoinArea() {
        home_minimap.getMapAsync {
            it.animateCamera(CameraUpdateFactory.newLatLngBounds(config.collectionAreaBounds, 10))
            it.addPolygon(config.collectionAreaPolygon
                    .strokeColor(Color.RED)
                    .fillColor(Color.TRANSPARENT))
        }
    }

    private fun moveToUserLocation() {
        Timber.i("Moving to user location")
        locationProvider.addListener(object : LocationListener.SimpleLocationListener {
            override fun locationUpdate(location: Location) {
                home_minimap.getMapAsync { it.animateCamera(location.asCameraUpdate()) }
                locationProvider.removeListener(this)
            }
        })
    }

    @SuppressLint("MissingPermission")
    private fun initLocationSystem() {
        locationProvider.start()
        permissionsManager = PermissionsManager(this)
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            home_minimap.getMapAsync {
                val lc = it.locationComponent
                lc.isLocationComponentEnabled = true
                val engine = LocationListeningEngine(locationProvider)
                engine.activate()
                lc.activateLocationComponent(this, engine)
                lc.renderMode = RenderMode.NORMAL
            }
            initGeofence()
        } else {
            permissionsManager.requestLocationPermissions(this)
        }
    }

    @SuppressLint("MissingPermission")
    private fun initGeofence() {
        val geofencingClient = LocationServices.getGeofencingClient(this)
        val bounds = config.collectionAreaBounds
        val center = bounds.center
        val radius = max(bounds.longitudeSpan, bounds.latitudeSpan)
        val fence = Geofence.Builder().setRequestId("coinz_geofence_id")
                .setCircularRegion(center.latitude, center.longitude, radius.toFloat())
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                .build()

        val intent = Intent(this, GeofenceTransitionsIntentService::class.java)
        geofencingClient.addGeofences(GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofence(fence)
        }.build(), PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT))

    }

    private fun bindViewModel() {
        println("Mock is $vm")
        vm.bind()
        vm.user.observe(this, Observer {
            user_email.text = it.email
        })
        vm.collectionInfo.observe(this, Observer {
            map_collection_info.text =
                    resources.getQuantityString(R.plurals.home_coin_collection_info,
                            it.numCollected, it.numCollected, it.numRemaining)
        })
        vm.coins.observe(this, Observer { coins ->
            home_minimap.getMapAsync {
                vm.mapMarkers(coins.zip(it.addMarkers(coins.map { coin -> coinToMarkerOption(this, coin) })).toMap().toMutableMap())
            }

        })
        vm.threads.observe(this, Observer {
            threadsAdapter.setThreads(it)
        })
        vm.recentlyBanked.observe(this, Observer {
            bankedAdapter.transactions = it
        })
        vm.bankInfo.observe(this, Observer {
            coin_bank_info.text = resources.getQuantityString(
                    R.plurals.bank_coins_banked_info,
                    it.numCollected,
                    it.numCollected,
                    it.numRemaining)
        })
        vm.actions.observe(this, Observer {
            when (it) {
                is HomeViewModel.HomeAction.BeginLoginFlow -> beginLoginFlow()
                is HomeViewModel.HomeAction.RemoveMarker -> home_minimap.getMapAsync { map ->
                    map.removeMarker(it.marker)
                }
                is HomeViewModel.HomeAction.ClearMarkers -> home_minimap.getMapAsync { map ->
                    map.markers.forEach { marker -> map.removeMarker(marker) }
                }
            }
        })
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

    private fun openThread(thread: Thread) {
        val intent = Intent(this, ThreadActivity::class.java)
        intent.putExtra(ThreadActivity.EXTRA_THREAD, thread)
        startActivity(intent)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == rcLogin) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                Timber.i("Sign in response ok")
                val user = FirebaseAuth.getInstance().currentUser
                user?.let {
                    Timber.i("Response details ${response?.idpToken} User details ${it.uid}")
                    vm.userLoggedIn(it.uid, it.email!!)
                }

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
        locationProvider.pause()
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