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

class MainActivity : AppCompatActivity() {
    private var downloadID: Long = 0
    enum class Links (val title: Int, val link: String) {
        GLIDE(R.string.glide, "https://github.com/bumptech/glide"),
        RETROFIT(R.string.retrofit,"https://github.com/square/retrofit"),
        LOADAPP(R.string.LoadApp,"https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter")
    }
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

    private fun showToastMessage(message: Int) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT)
            .show()
    }

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
