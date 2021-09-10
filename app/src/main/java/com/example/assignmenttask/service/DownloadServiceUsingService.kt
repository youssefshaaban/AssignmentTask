package com.example.assignmenttask.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.icu.text.CaseMap.Title
import androidx.core.app.JobIntentService
import com.example.assignmenttask.data.DataRepositery
import com.example.assignmenttask.data.PrecentageData

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import androidx.localbroadcastmanager.content.LocalBroadcastManager

import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.example.assignmenttask.App.Companion.context
import com.example.assignmenttask.R
import com.example.assignmenttask.utils.NotificationHelper
import dagger.android.AndroidInjection
import java.util.concurrent.TimeUnit.MILLISECONDS

class DownloadServiceUsingService : Service() {
  @Inject lateinit var dataRepositery: DataRepositery
  @Inject lateinit var composit: CompositeDisposable

  // var movie: Movie? = null
  // var position: Int? = null
  // var NOTIFICATION_ID: Int? = 1

  override fun onCreate() {
    super.onCreate()
    AndroidInjection.inject(this)
  }

  override fun onDestroy() {
    super.onDestroy()
    composit.clear()
  }

  override fun onBind(intent: Intent?): IBinder? {
    return null
  }

  private fun onErrors(throwable: Throwable, notificationId: Int, position: Int) {
    val notifcatioHelper = NotificationHelper(this)
    notifcatioHelper.cancelNotification(notificationId)
    sendFailureMessageToActivity(throwable.toString(), position)
  }

  private fun onProgress(title: String?, it: PrecentageData, NOTIFICATION_ID: Int, postion: Int) {
    // val progressIntent = Intent(this, FileProgressReceiver::class.java)
    // progressIntent.action = ACTION_PROGRESS_NOTIFICATION
    // progressIntent.putExtra("notificationId", NOTIFICATION_ID)
    // progressIntent.putExtra(TITTLE, title)
    var progress = 0
    if (it.total != -1L) {
      progress = (it.currentPrecntage.toDouble() / it.total.toDouble()).toInt() * 100
     // progressIntent.putExtra("progress", progress)
    } else {
    //  count = +.1
    //  progress = (100 * count).toInt()
   //   progressIntent.putExtra("progress",progress)
    }
    //sendBroadcast(progressIntent)
    // val notifcatioHelper = NotificationHelper(this)
    // val notify=notifcatioHelper.getNotification(title, progress = progress,body = "$progress %")
    // Log.e(this.toString(),NOTIFICATION_ID.toString())
    // notifcatioHelper.notify(NOTIFICATION_ID,notify)
    Log.e("onSuccess $title",NOTIFICATION_ID.toString())
    sendSuccesMessageToActivity(false, it, postion)
  }

  private fun onSuccess(title: String?, NOTIFICATION_ID: Int, postion: Int) {
    // val successIntent = Intent(this, FileProgressReceiver::class.java)
    // // successIntent.action = ACTION_Downloaded
    // // successIntent.putExtra("notificationId", NOTIFICATION_ID)
    // // successIntent.putExtra("progress", 100)
    // // successIntent.putExtra(TITTLE, title)
    // val notifcatioHelper = NotificationHelper(this)
    // val notify=notifcatioHelper.getNotification(title,getString(R.string.file_upload_successful),null)
    // notifcatioHelper.notify(NOTIFICATION_ID,notify!!)
    Log.e("onSuccess $title",NOTIFICATION_ID.toString())
    sendSuccesMessageToActivity(true, null, postion)
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    val position = intent?.getIntExtra("position", 0)
    val url = intent?.getStringExtra("url")
    val tittle = intent?.getStringExtra(TITTLE)
    val NOTIFICATION_ID = intent?.getIntExtra("NOTIFICATION_ID", 0)
    composit.add(
      dataRepositery.downloadTaskOkHttp2(url!!)
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          onProgress(tittle, it, NOTIFICATION_ID!!, position!!)
        }, {
          onErrors(throwable = it, NOTIFICATION_ID!!, position!!)
        }, {
          onSuccess(tittle, NOTIFICATION_ID!!, position!!)
        })
    )
    return START_STICKY
  }

  private fun sendSuccesMessageToActivity(isCompleted: Boolean, l: PrecentageData?, position: Int) {
    val intent = Intent("PROGRESS_DOWNLOAD")
    val b = Bundle()
    l?.let {
      b.putParcelable("object", l)
    }
    b.putInt("position", position)
    b.putBoolean("isCompleted", isCompleted)
    intent.putExtras(b)
    LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
  }

  private fun sendFailureMessageToActivity(message: String, position: Int) {
    val intent = Intent("FAILURE_DOWNLOAD")
    intent.putExtra("position", position)
    intent.putExtra("message", message)
    LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
  }
}