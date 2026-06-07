package org.nla.phototovideoai.features.fcm

import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.appmetrica.analytics.push.provider.firebase.AppMetricaMessagingService
import org.nla.phototovideoai.MainActivity
import org.nla.phototovideoai.features.notification.NotificationManager

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val notificationManager by lazy { NotificationManager(this) }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (AppMetricaMessagingService.isNotificationRelatedToSDK(remoteMessage)) {
            // Push AppMetrica
            AppMetricaMessagingService().processPush(this, remoteMessage)
            return
        }

        val data = remoteMessage.data
        if (data.isEmpty()) return

        val title = data["title"] ?: "Уведомление"
        val message = data["message"] ?: ""
        val id = data["id"]?.toIntOrNull() ?: System.currentTimeMillis().toInt()

        sendNotification(id, title, message)
    }

    private fun sendNotification(id: Int, title: String, message: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = notificationManager.createNotification(
            channelId = NotificationManager.CHANNEL_NOTIFICATIONS_ID,
            title = title,
            message = message,
            priority = NotificationCompat.PRIORITY_MAX,
            contentPendingIntent = pendingIntent
        )

        notificationManager.sendNotification(id, notification)
    }

    override fun onNewToken(token: String) {
        AppMetricaMessagingService().processToken(this, token)
    }
}