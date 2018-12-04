package com.tpb.coinz.data.location.background

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
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
import javax.inject.Inject

class ForegroundLocationService : Service(), CoinCollector.CoinCollectorListener {

    private val id = 353
    private var rootNotif: Notification? = null
    private val channel = "coinz_collection_channel"
    private val SUMMARY_ID = 534
    private val group = "coin_collection_group"

    @Inject lateinit var userCollection: UserCollection
    @Inject lateinit var coinCollection: CoinCollection
    @Inject lateinit var collector: CoinCollector


    override fun onBind(p0: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        moveToForeground()
        (application as App).serviceComponent.inject(this)

        collector.setCoinCollection(coinCollection, userCollection.getCurrentUser())
        collector.addCollectionListener(this)
        //TODO: Collector pause method
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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        collector.start()
        return super.onStartCommand(intent, flags, startId)
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
        collector.removeCollectionListener(this)
        collector.dispose()
    }
}