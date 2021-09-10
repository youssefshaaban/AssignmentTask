package com.example.assignmenttask.service

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.assignmenttask.R
import com.example.assignmenttask.ui.MainActivity
import com.example.assignmenttask.utils.NotificationHelper
import java.util.*

val TITTLE = "title"
val ACTION_CLEAR_NOTIFICATION = "ACTION_CLEAR_NOTIFICATION"
val ACTION_PROGRESS_NOTIFICATION = "ACTION_PROGRESS_NOTIFICATION"
val ACTION_Downloaded = "ACTION_DOWNLOADED"
class FileProgressReceiver : BroadcastReceiver() {
  private val TAG = "FileProgressReceiver"
  var mNotificationHelper: NotificationHelper? = null
 // val NOTIFICATION_ID = 1
  var notification: NotificationCompat.Builder? = null
  lateinit var title: String
  override fun onReceive(context: Context?, intent: Intent?) {
    mNotificationHelper = NotificationHelper(context!!)
    // Get notification id
    // Get notification id
    val notificationId = intent!!.getIntExtra("notificationId", 1)
    title = intent.getStringExtra(TITTLE) ?: ""
    // Receive progress
    // Receive progress
    val progress = intent.getIntExtra("progress", 0)

    when (Objects.requireNonNull(intent.action)) {
      ACTION_PROGRESS_NOTIFICATION -> {
        notification = mNotificationHelper?.getNotification(
          title,
          context.getString(R.string.in_progress), progress
        )
        mNotificationHelper?.notify(notificationId, notification!!)
      }
      ACTION_CLEAR_NOTIFICATION -> {
        mNotificationHelper?.cancelNotification(notificationId)
      }
      ACTION_Downloaded -> {
        notification = mNotificationHelper!!.getNotification(
          title,
          context.getString(R.string.file_upload_successful), null
        )
        mNotificationHelper!!.notify(notificationId, notification!!)
      }
    }
  }
}