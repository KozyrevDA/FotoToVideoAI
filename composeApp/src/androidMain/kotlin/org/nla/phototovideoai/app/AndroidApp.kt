package org.nla.phototovideoai.app

import AndroidSharedPreferences
import android.app.Application
import android.app.Notification
import com.msilimon.vkauthdonate.VkApi
import org.nla.phototovideoai.R
import org.nla.phototovideoai.billing.google.GoogleBilling
import org.nla.phototovideoai.billing.rustore.RuStoreBilling
import org.nla.phototovideoai.features.notification.NotificationManager

class AndroidApp : Application() {
    private val sharedPreferences = AndroidSharedPreferences(this)
    var ruStoreBilling: RuStoreBilling? = null
        private set

    var googleBilling: GoogleBilling? = null
        private set

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        VkApi.initializeApp(
            appContext = this,
            groupIdVk = "233749626",
            sharedPrefsName = SHARED_PREFS_NAME
        )
        createChannelsAndroid()

        ruStoreBilling = RuStoreBilling()
        googleBilling = GoogleBilling()
    }

    private fun createChannelsAndroid() {
        if (sharedPreferences.getNotificationChannelsExist())
            return

        val notificationManager = NotificationManager(this)

        notificationManager.createNotificationChannel(
            channelId = NotificationManager.CHANNEL_NOTIFICATIONS_ID,
            channelName = getString(R.string.channel_notification_name),
            descriptionChannel = getString(R.string.channel_notification_desc),
            importance = android.app.NotificationManager.IMPORTANCE_HIGH,
            lockscreenVisibilityChannel = Notification.VISIBILITY_PUBLIC
        )

        sharedPreferences.putNotificationChannelsExist(true)
    }

    companion object {
        private const val SHARED_PREFS_NAME = "shared_prefs_app"

        lateinit var INSTANCE: AndroidApp
            private set
    }
}