package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

private const val NOTIFICATION_ID = 1

const val TITLE = "title"
const val SUCCESS = "success"

fun NotificationManager.sendNotification(
    applicationContext: Context,
    title: String,
    isSuccess: Boolean
) {
    val DAIntent = Intent(applicationContext, DetailActivity::class.java).apply {
        putExtra(TITLE, title)
        putExtra(SUCCESS, isSuccess)
    }

    val buttonPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        DAIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

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

    notify(NOTIFICATION_ID, builder.build())
}
