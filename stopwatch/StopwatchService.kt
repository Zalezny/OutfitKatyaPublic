package com.example.outfitapp.stopwatch

import android.app.Notification.FOREGROUND_SERVICE_IMMEDIATE
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.example.outfitapp.*
import com.example.outfitapp.util.StopwatchUtil

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class StopwatchService :  LifecycleService() {

    companion object {
        //is on or off (LiveData)
        val stopwatchEvent = MutableLiveData<StopwatchEvent>()
        //time in milliseconds (LiveData)
        val timerInMillis = MutableLiveData<Long>()
        //date in json format (just String) (LiveData)
        val dateJSONFormat = MutableLiveData<String>()
    }

    //check is service stopped
    private var isServiceStopped = false

    //Timer properties
    private var lapTime = 0L
    private var timeStarted = 0L

    //date of start timer
    private var dateStart = ""

    //Variables between intents exchange
    private var outfitName = ""
    private var chosenID = ""

    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationBuilder: NotificationCompat.Builder

    override fun onCreate() {
        super.onCreate()
        //reset stopwatch on Create.
        initValues()



    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //extras will be use when user click on notification
        chosenID = intent!!.getStringExtra("chosenID")!!
        outfitName = intent.getStringExtra("titleOutfit")!!
        //action service (set in StoperFragment)
        intent.let {
            when(it.action) {
                ACTION_START_SERVICE -> {
                    startForegroundService()
                }
                ACTION_STOP_SERVICE -> {
                    stopService()
                }
                else -> {
                    return@let
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundService() {
        //send info about start Stopwatch
        stopwatchEvent.postValue(StopwatchEvent.START)
        startTimer()

        //sdk must be over of oreo!
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        startForeground(NOTIFICATION_ID, notificationBuilder.build())

        //observer of timerInMillis to change timer in notification text
        timerInMillis.observe(this) {
            if(!isServiceStopped) {
                notificationBuilder.setContentText(
                    StopwatchUtil.getFormattedTime(it, false)
                )
                //notify manager about changes
                notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
            }
        }

    }

    private fun stopService() {
        isServiceStopped = true
        //send date of start
        dateJSONFormat.postValue(dateStart)
        //set values to default value
        initValues()
        //cancel foreground notification
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(
            NOTIFICATION_ID
        )
        stopForeground(true)
        //stop this service (itself)
        stopSelf()
    }

    private fun initValues() {
        //finish stopwatch
        stopwatchEvent.postValue(StopwatchEvent.END)
        //set time on 0
        timerInMillis.postValue(0L)
    }

    private fun createNotificationChannel() {
        //channel for notification
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )

        //init notification Manager
        notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //add channel to notification Manager
        notificationManager.createNotificationChannel(channel)


        //init notification Builder
        notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.stoper_go)
            .setContentTitle(outfitName)
            .setContentText("00:00:00")
            .setForegroundServiceBehavior(FOREGROUND_SERVICE_IMMEDIATE)
            .setContentIntent(pendingIntentTimeActivity())

    }

    private fun pendingIntentTimeActivity(): PendingIntent {

        val intent = Intent(this, TimeActivity::class.java).apply {
            //if activity is on the top of the history stack, it wont be launch
            this.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        //intents needs in TimeActivity/StoperFragment to start
        intent.putExtra("title", outfitName)
        intent.putExtra("chosenOutfitID", chosenID)

        //intent for notification after click on it

        return PendingIntent.getActivity(
            this,
            420,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }


    private fun startTimer() {
        //set start time the current time in millis
        timeStarted = System.currentTimeMillis()
        //set date of start timer with format json
        dateStart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"))
        //open new task for timer
        CoroutineScope(Dispatchers.Main).launch {
        //while stopwatchEvent.value will be START and service will be not stopped
            while (stopwatchEvent.value!! == StopwatchEvent.START && !isServiceStopped) {
                //the timer time is current time minus time of started
                lapTime = System.currentTimeMillis() - timeStarted
                //send info about timer time (for ui)
                timerInMillis.postValue(lapTime)
                //delay of 50 milliseconds
                delay(50L)
            }
        }
    }


}