package com.example.notificationscheduler

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class NotificationJobService : JobService() {
    private val NOTIFICATION_ID = 1
    private val CHANNEL_ID = "notification_scheduler_channel"

    override fun onStartJob(params: JobParameters?): Boolean {
        // Create notification channel for Android O and above
        createNotificationChannel()

        // Create the notification intent
        val contentIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            contentIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Scheduled Job")
            .setContentText("Your scheduled job is running!")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        // Get the notification manager and send notification
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, builder.build())

        // Job is finished - return false as we don't need to do work on a separate thread
        jobFinished(params, false)
        return false
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        // Return true to reschedule the job
        return true
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notification Scheduler"
            val description = "Channel for Notification Scheduler"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
} 