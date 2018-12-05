package com.tpb.coinz.data.location.background

import android.app.*
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.tpb.coinz.App
import com.tpb.coinz.data.coin.Coin
import com.tpb.coinz.data.coin.CoinCollector
import com.tpb.coinz.data.coin.Map
import com.tpb.coinz.data.coin.collection.CoinCollection
import com.tpb.coinz.data.users.UserCollection
import com.tpb.coinz.view.home.HomeActivity
import org.koin.android.ext.android.inject


class ForegroundLocationService : Service(), CoinCollector.CoinCollectorListener, Application.ActivityLifecycleCallbacks {

    private val id = 353
    private var rootNotif: Notification? = null
    private val channel = "coinz_collection_channel"
    private val SUMMARY_ID = 534
    private val group = "coin_collection_group"

    val userCollection: UserCollection by inject()
    val coinCollection: CoinCollection by inject()
    val collector: CoinCollector by inject()


    override fun onBind(p0: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        moveToForeground()
        application.registerActivityLifecycleCallbacks(this)
        collector.setCoinCollection(coinCollection, userCollection.getCurrentUser())
        collector.addCollectionListener(this)
    }

    private fun moveToForeground() {
        val notifIntent = Intent(this, HomeActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notifIntent, 0)
        rootNotif = NotificationCompat.Builder(this, channel)
                .setContentTitle("title for notification")
                .setContentText("App is watching you")
                .setGroup(group)
                .setContentIntent(pendingIntent).build()
        startForeground(id, rootNotif)
    }

    private fun updateSummaryNotification(collected: List<Coin>): Notification {
        return NotificationCompat.Builder(this, channel)
                .setContentTitle("Summary notification")
                .setContentText("Some summary information")
                .setGroup(group)
                .setGroupSummary(true)
                .build()
    }

    override fun coinsCollected(collected: List<Coin>) {
        //TODO: Notification group for collected notifications
        //TODO: Generate summary
        //https://developer.android.com/training/notify-user/group
        NotificationManagerCompat.from(this).apply {
            collected.forEach {
                notify(it.markerColor, NotificationCompat.Builder(this@ForegroundLocationService, channel)
                        .setContentTitle(it.currency.name)
                        .setContentText(it.id)
                        .setGroup(group)
                        .build())
            }
            notify(SUMMARY_ID, updateSummaryNotification(collected))
        }
    }

    override fun mapLoaded(map: Map) {
    }

    override fun notifyReloading() {
    }

    override fun onDestroy() {
        super.onDestroy()
        application.unregisterActivityLifecycleCallbacks(this)
        collector.removeCollectionListener(this)
        collector.dispose()
    }

    private var activityCount = 0

    private fun run() {
        collector.addCollectionListener(this)
    }

    private fun pause() {
        // If the app is in the foreground, the CoinCollector will still be running
        // but we don't have to care about
        collector.removeCollectionListener(this)
    }

    override fun onActivityPaused(p0: Activity?) {
        activityCount--
        if (activityCount == 0) run()
    }

    override fun onActivityResumed(activity: Activity?) {
        activityCount++
        pause()
    }

    // We don't care about any of these
    override fun onActivityDestroyed(p0: Activity?) {

    }

    override fun onActivityStarted(p0: Activity?) {
    }

    override fun onActivitySaveInstanceState(p0: Activity?, p1: Bundle?) {
    }

    override fun onActivityStopped(p0: Activity?) {
    }

    override fun onActivityCreated(p0: Activity?, p1: Bundle?) {
    }
}