package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    private var url = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        /**Button welcome animation!*/
        custom_button.alpha = 0f
        custom_button.translationY = 50f
        custom_button.animate().alpha(1f).translationYBy(-100f).duration = 1000

        radio_glide.setOnClickListener {
            custom_button.setState(ButtonState.Clickable)
            url = GLIDE_URL
        }

        radio_udacity.setOnClickListener {
            custom_button.setState(ButtonState.Clickable)
            url = UDACITY_URL
        }

        radio_retrofit.setOnClickListener {
            custom_button.setState(ButtonState.Clickable)
            url = RETROFIT_URL
        }

        custom_button.setOnClickListener {
            if (url != "") {
                custom_button.setState(ButtonState.Loading)
                custom_button.animations()
                Toast.makeText(this, "File is downloading", Toast.LENGTH_SHORT).show()
                Log.d("TAG","IfBtnClick")
                //TODO download()
            } else {
                Log.d("TAG","ElseBtnClick")
                Toast.makeText(this, "Please select a file to download", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        }
    }

    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {
        private const val UDACITY_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val GLIDE_URL =
            "https://github.com/bumptech/glide"
        private const val RETROFIT_URL =
            "https://github.com/square/retrofit"
        private const val CHANNEL_ID = "channelId"
    }


}
