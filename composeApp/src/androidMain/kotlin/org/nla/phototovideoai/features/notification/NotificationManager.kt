package org.nla.phototovideoai.features.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import org.nla.phototovideoai.R

class NotificationManager(private val context: Context) {
    companion object {
        const val CHANNEL_NOTIFICATIONS_ID = "channel_id_notifications"
    }

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    /**
     * Создает уведомление.
     *
     * @param channelId Идентификатор канала.
     * @param title Заголовок уведомления.
     * @param message Текст уведомления.
     * @param smallIcon Идентификатор иконки уведомления.
     * @param priority Приоритет уведомления.
     * @param contentPendingIntent Интент для открытия AlarmActivity при нажатии на уведомление.
     * @param fullScreenIntent Интент для открытия AlarmActivity при запуске уведомления.
     * @param action Интент для кнопки в уведомлении.
     */
    fun createNotification(
        channelId: String,
        title: String,
        message: String,
        smallIcon: Int = R.drawable.ic_notification,
        largeIcon: Int = R.drawable.avatar,
        priority: Int = NotificationCompat.PRIORITY_DEFAULT,
        contentPendingIntent: PendingIntent,
        fullScreenIntent: PendingIntent? = null,
        actions: List<Action> = emptyList(),
    ): Notification {
        return NotificationCompat.Builder(context, channelId)
            .setSmallIcon(smallIcon)
            .setContentTitle(title)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, largeIcon))
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(priority)
            .setContentIntent(contentPendingIntent)
            .run {
                if (fullScreenIntent != null) {
                    setFullScreenIntent(fullScreenIntent, true)
                }
                if (actions.isNotEmpty()) {
                    actions.forEach { action ->
                        addAction(action.icon, action.title, action.intent)
                    }
                }
                this
            }
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .build()
    }

    fun sendNotification(notificationId: Int, notification: Notification) {
        notificationManager.notify(notificationId, notification)
    }

    /**
     * Создает канал уведомлений (требуется для Android 8.0 и выше).
     *
     * @param channelId Идентификатор канала.
     * @param channelName Имя канала.
     * @param importance Важность канала (NotificationManager.IMPORTANCE_*).
     */
    fun createNotificationChannel(
        channelId: String,
        channelName: String,
        importance: Int = NotificationManager.IMPORTANCE_DEFAULT,
        descriptionChannel: String = channelName,
        lockscreenVisibilityChannel: Int = Notification.VISIBILITY_PRIVATE,
        isAlarm: Boolean = false,
    ) {
        val channel = NotificationChannel(
            channelId,
            channelName,
            importance
        ).apply {
            enableVibration(true)
            description = descriptionChannel
            lockscreenVisibility = lockscreenVisibilityChannel

            when (isAlarm) {
                true -> setSound(null, null)
                false -> {
                    val audioAttributes = AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()

                    setSound(
                        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                        audioAttributes
                    )
                }
            }
        }
        notificationManager.createNotificationChannel(channel)
    }

    data class Action(
        val icon: Int,
        val title: String,
        val intent: PendingIntent? = null,
    )
}