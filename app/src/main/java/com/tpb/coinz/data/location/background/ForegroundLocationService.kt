package com.tpb.coinz.data.location.background

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.tpb.coinz.R
import com.tpb.coinz.data.coin.Coin
import com.tpb.coinz.data.coin.Map
import com.tpb.coinz.data.coin.collection.CoinCollector
import com.tpb.coinz.view.home.HomeActivity
import org.koin.android.ext.android.inject
import timber.log.Timber


class ForegroundLocationService : Service(), CoinCollector.CoinCollectorListener {

    private val id = 353
    private var rootNotif: Notification? = null
    private val CHANNEL_ID = "coinz_collection_channel"
    private val SUMMARY_ID = 534
    private val group = "coin_collection_group"

    private val collector: CoinCollector by inject()
    private val totalCollected = mutableListOf<Coin>()

    override fun onBind(p0: Intent?): IBinder? = null

    companion object {

        private var listener: ((Boolean) -> Unit)? = null
        var isActivityInForeground: Boolean = false
            set(value) {
                field = value
                listener?.invoke(value)
            }


        fun start(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Timber.i("Starting foreground service")
                context.startForegroundService(Intent(context, ForegroundLocationService::class.java))
            } else {
                Timber.i("Starting service")
                context.startService(Intent(context, ForegroundLocationService::class.java))
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        Timber.i("Location service starting")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
        moveToForeground()
        collector.addCollectionListener(this)
        listener = {
            if (it) {
                collector.addCollectionListener(this)
            } else {
                // If the app is in the foreground, the CoinCollector will still be running
                // but we don't want to post notifications
                collector.removeCollectionListener(this)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(CHANNEL_ID, "GPS tracking", NotificationManagerCompat.IMPORTANCE_NONE)
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
    }

    private fun moveToForeground() {
        createRootNotif(getString(R.string.text_collecting_in_background),
                getString(R.string.content_collecting_in_background)
        )
        startForeground(id, rootNotif)
    }

    private fun createRootNotif(title: String, content: String) {
        val notifIntent = Intent(this, HomeActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notifIntent, 0)
        rootNotif = NotificationCompat.Builder(this, CHANNEL_ID)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setSmallIcon(R.drawable.ic_coins)
                .setContentTitle(title)
                .setContentText(content)
                .setGroup(group)
                .setContentIntent(pendingIntent).build()
    }

    private fun updateSummaryNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.text_collecting_in_background))
                .setContentText(resources.getQuantityString(
                        R.plurals.text_total_collected_in_background,
                        totalCollected.size, totalCollected.size))
                .setGroup(group)
                .setGroupSummary(true)
                .build()
    }

    override fun coinsCollected(collected: List<Coin>) {
        //https://developer.android.com/training/notify-user/group
        NotificationManagerCompat.from(this).apply {
            collected.forEach {
                notify(it.markerColor, NotificationCompat.Builder(this@ForegroundLocationService, CHANNEL_ID)
                        .setContentTitle(it.currency.name)
                        .setContentText(it.id)
                        .setSmallIcon(it.currency.img)
                        .setGroup(group)
                        .build())
            }
            totalCollected.addAll(collected)
            notify(SUMMARY_ID, updateSummaryNotification())
        }
    }

    override fun mapLoaded(map: Map) {
        createRootNotif(getString(R.string.text_collecting_in_background),
                getString(R.string.content_collecting_in_background)
        )
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(id, rootNotif)
    }

    override fun notifyReloading() {
        createRootNotif(getString(R.string.text_reloading_map), "")
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(id, rootNotif)
    }

    override fun onDestroy() {
        super.onDestroy()
        collector.removeCollectionListener(this)
        collector.dispose()
        listener = null
    }


}