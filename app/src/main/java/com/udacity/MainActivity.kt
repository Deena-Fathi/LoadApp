package com.udacity

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.File

/**
 * An Android app that allows the user to download files from different links.
 * The file can be selected from a radio group, and a progress bar indicates the download progress.
 * Once the download is complete, a notification is sent to the user.
 */
class MainActivity : AppCompatActivity() {
    //Storing the ID of the current download. It is initially set to 0.
    private var downloadID: Long = 0

    /**
     * This is an enum class that defines the available download links.
     * Each link has a title and a URL.
     */
    enum class Links(val title: Int, val link: String) {
        GLIDE(R.string.glide, "https://github.com/bumptech/glide"),
        RETROFIT(R.string.retrofit, "https://github.com/square/retrofit"),
        LOADAPP(
            R.string.LoadApp,
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
        )
    }

    /**
     * This function is called when the activity is created.
     * It sets the content view to the main layout file, sets the support action bar,
     * and registers a broadcast receiver to receive download complete events.
     * It sets an on-click listener to the download button, which first creates a notification channel,
     * then checks if a file is selected from the radio group. If a file is selected, the download function
     * is called with the appropriate link. Otherwise, a toast message is displayed to remind the
     * user to select a file.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        download_button.setOnClickListener {
            createChannel(getString(R.string.channel_id), getString(R.string.button_name))
            if (radioGroup.checkedRadioButtonId == -1)
                showToastMessage(R.string.please_select_file)
            else {
                when (radioGroup.checkedRadioButtonId) {
                    radio_glide.id -> download(Links.GLIDE)
                    radio_load_app.id -> download(Links.LOADAPP)
                    radio_retrofit.id -> download(Links.RETROFIT)
                }
                Log.i("Main Activity", "RadioGroup.OnCheckedChangeListener: checked")

            }
        }
    }

    /**
     * This function takes an integer message as an input and displays a
     * short-duration toast message to the user.
     */
    private fun showToastMessage(message: Int) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT)
            .show()
    }

    /**
     * The registerReceiver() method registers a BroadcastReceiver instance to listen for events
     * when a download is completed. When a download is complete, the onReceive() method of the
     * BroadcastReceiver instance is called. In this method, the code checks if the download ID
     * matches the downloadID value that was set when the download was initiated. If it does,
     * it queries the DownloadManager to get information about the completed download,
     * and then calls the sendNotification() method to display a notification to the user.
     */
    private val receiver = object : BroadcastReceiver() {
        @SuppressLint("Range")
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadID == id) {
                val query = DownloadManager.Query()
                query.setFilterById(id)
                val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                val cursor: Cursor = downloadManager.query(query)

                if (cursor.moveToFirst()) {
                    val success =
                        cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    val isSuccess = success == DownloadManager.STATUS_SUCCESSFUL
                    val downloadTitle =
                        cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE))
                    sendNotification(isSuccess, downloadTitle)
                }
                download_button.downloadCompleted()
            }
        }
    }

    /**
     * The download() method is used to initiate a download using the DownloadManager.
     * It sets various properties of the download request, such as the download URL,
     * the download title, and the description. It then enqueues the download request
     * using the DownloadManager.
     */
    private fun download(link: Links) {
        download_button.buttonState = ButtonState.Loading
        val request =
            DownloadManager.Request(Uri.parse(link.link))
                .setTitle(getString(link.title))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    /**
     * The createChannel() method is used to create a notification channel for displaying notifications.
     * If the device is running Android Oreo or later, a NotificationChannel is created with the
     * specified ID, name, and importance level. The channel is then configured with various properties,
     * such as whether it supports lights and vibration, the light color, and the channel description.
     * Finally, the channel is added to the NotificationManager.
     */
    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.description = getString(R.string.notification_description)

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)

        }
    }

    /**
     * The sendNotification() method is used to display a notification to the user when a download is complete.
     * It first cancels any existing notifications, and then creates a new notification using the
     * NotificationCompat.Builder class. The notification title and message are set based on whether
     * the download was successful or not. The notification is then displayed using the NotificationManager.
     */
    private fun sendNotification(isSuccess: Boolean, downloadTitle: String) {
        val notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.cancelAll()
        notificationManager.sendNotification(
            this,
            downloadTitle, isSuccess
        )
    }

}
