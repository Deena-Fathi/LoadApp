package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

private const val NOTIFICATION_ID = 1

const val TITLE = "title"
const val SUCCESS = "success"

/**
 * The sendNotification function takes three arguments: the applicationContext,
 * the title of the notification, and a Boolean value indicating whether the notification
 * represents a successful download.
 */
fun NotificationManager.sendNotification(
    applicationContext: Context,
    title: String,
    isSuccess: Boolean
) {
    /**
     * Inside the function, an Intent is created for the notification, specifying the DetailActivity
     * class as the destination. The title and success parameters are added to the intent as extra data.
     */
    val DAIntent = Intent(applicationContext, DetailActivity::class.java).apply {
        putExtra(TITLE, title)
        putExtra(SUCCESS, isSuccess)
    }

    /**
     * The PendingIntent class is then used to create an Intent that can be executed by the
     * notification when the user taps on it. The FLAG_UPDATE_CURRENT flag is set to reuse
     * the pending intent if one already exists, and the FLAG_IMMUTABLE flag is set to prevent
     * the pending intent from being modified by other components.
     */
    val buttonPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        DAIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    /**
     * NotificationCompat.Builder object is created, which configures the details of the notification.
     * The builder sets the title, description, icon, priority, and an action button.
     * The action button has a label, an icon, and the previously created PendingIntent.
     */
    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.channel_id)
    ).apply {
        setContentTitle(applicationContext.getString(R.string.notification_title))
        setContentText(applicationContext.getString(R.string.notification_description))
        setAutoCancel(true)
        priority = NotificationCompat.PRIORITY_HIGH
        setSmallIcon(R.drawable.ic_assistant_black_24dp)
        addAction(
            R.drawable.ic_assistant_black_24dp,
            applicationContext.getString(R.string.notification_button),
            buttonPendingIntent
        )
    }

    /**
     * the NotificationManager calls the notify method to send the notification,
     * passing the notification ID and the notification builder.
     * The notification ID is a constant value of 1, defined at the beginning of the file.
     */
    notify(NOTIFICATION_ID, builder.build())
}
