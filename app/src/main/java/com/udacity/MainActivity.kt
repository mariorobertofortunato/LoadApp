package com.udacity

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private var downloadStatus = ""

    private lateinit var notificationManager: NotificationManager

    private var url = ""
    private var filename = ""
    private var desc =""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        /**Initialize the notification channel*/
        createChannel(CHANNEL_ID, getString(R.string.notification_channel_name))

        /**Button "welcome" animation*/
        custom_button.alpha = 0f
        custom_button.translationY = 50f
        custom_button.animate().alpha(1f).translationYBy(-100f).duration = 1000

        /**Click listeners (Radio button + Custom Btn)*/
        radio_glide.setOnClickListener {
            custom_button.setState(ButtonState.Clickable)
            url = GLIDE_URL
            filename = getString(R.string.glide)
            desc = getString(R.string.glide_desc)
        }
        radio_udacity.setOnClickListener {
            custom_button.setState(ButtonState.Clickable)
            url = UDACITY_URL
            filename = getString(R.string.loadapp)
            desc = getString(R.string.loadapp_desc)
        }
        radio_retrofit.setOnClickListener {
            custom_button.setState(ButtonState.Clickable)
            url = RETROFIT_URL
            filename = getString(R.string.retrofit)
            desc = getString(R.string.retrofit_desc)
        }
        custom_button.setOnClickListener {
            if (url != "") {
                custom_button.setState(ButtonState.Loading)
                custom_button.animations()
                Toast.makeText(this, "File is downloading", Toast.LENGTH_SHORT).show()
                download()
                url = "" //reset the url after a file has been downloaded
            } else {
                Toast.makeText(this, "Please select a file to download", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**DOWNLOAD SECTION*/

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            //the id of the download
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            //download status check
            downloadStatus = if (downloadID == id) { "SUCCESS" } else { "DOWNLOAD FAILED" }
            radio_group.clearCheck()
            custom_button.stopAnimations()
            custom_button.setState(ButtonState.Clickable)

            //Received the file the notif is sent (create instance + start build&send method)
            notificationManager = ContextCompat.getSystemService(context!!,NotificationManager::class.java) as NotificationManager
            notificationManager.sendNotification(context.getText(R.string.notification_description).toString(),context)
        }
    }
    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(filename)
                .setDescription(desc)
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID = downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    /**NOTIFICATION SECTION*/

    //The method called to build (and send) the notif
    @SuppressLint("UnspecifiedImmutableFlag")
    fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context) {

                /**Building blocks of the notif*/
                //Create the content intent for the notif (= where clickin the notif will bring you (which activity))
                //passing some param I need in the destination activity
                val detailIntent = Intent(applicationContext, DetailActivity::class.java)
                    .putExtra("name",desc)
                    .putExtra("status",downloadStatus)

                //Create the Pending intent (The system will use the pending intent to open the activity specified in the contentIntent)
                val detailPendingIntent = PendingIntent.getActivity(
                    applicationContext,
                    NOTIFICATION_ID,
                    detailIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT)

                //Notif style (=image)
                val notifImage = BitmapFactory.decodeResource(applicationContext.resources,R.drawable.ic_assistant_black_24dp)
                val bigPicStyle = NotificationCompat.BigPictureStyle()
                    .bigPicture(notifImage)
                    .bigLargeIcon(null)

        /**Notification builder: assembles all the building blocks of the notification*/
        val builder = NotificationCompat.Builder(applicationContext, applicationContext.getString(R.string.notification_channel_id))
            .setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setContentTitle(applicationContext.getString(R.string.notification_title))
            .setContentText(messageBody)
            .setContentIntent(detailPendingIntent)
            .setAutoCancel(true)
            .setStyle(bigPicStyle)
            .setLargeIcon(notifImage)
            .setContentText("Download Complete!")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .addAction(R.drawable.ic_assistant_black_24dp, applicationContext.getString(R.string.notification_button), detailPendingIntent)

        notify(NOTIFICATION_ID, builder.build())
    }

    //the method called to build the notification channel
    @RequiresApi(Build.VERSION_CODES.O)
    fun createChannel(channelId: String, channelName: String) {

        //Create the channel with the passed param and "attributes"
        val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        notificationChannel.enableLights(true)
        notificationChannel.enableVibration(true)
        notificationChannel.lightColor = Color.RED
        notificationChannel.description = "Channel"

        notificationManager = ContextCompat.getSystemService(this, NotificationManager::class.java) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }

    companion object {
        private const val UDACITY_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val GLIDE_URL =
            "https://github.com/bumptech/glide"
        private const val RETROFIT_URL =
            "https://github.com/square/retrofit"
        private const val CHANNEL_ID =
            "channelId"
        private const val NOTIFICATION_ID = 0
    }
}
