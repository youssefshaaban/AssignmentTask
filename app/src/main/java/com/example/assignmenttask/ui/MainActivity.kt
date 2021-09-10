package com.example.assignmenttask.ui

import android.Manifest.permission
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build.VERSION
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.Factory
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.assignmenttask.R
import com.example.assignmenttask.data.Movie
import com.tbruyelle.rxpermissions2.RxPermissions
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import javax.inject.Inject
import android.content.IntentFilter
import com.example.assignmenttask.data.PrecentageData
import com.example.assignmenttask.service.DownloadService
import com.example.assignmenttask.service.TITTLE
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.assignmenttask.service.DownloadServiceUsingService

val FILTER_ACTION_KEY = "any_key"

class MainActivity : AppCompatActivity() {
  @Inject
  lateinit var factory: Factory
  lateinit var viewModel: MainViewModel
  lateinit var progress: ProgressBar
  lateinit var rv: RecyclerView
  val recevierSuccess=object :BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
       handleIntentSuccess(intent)
    }
  }

  private fun handleIntentSuccess(intent: Intent?) {
    val bundle=intent?.extras
    val position=bundle?.getInt("position",0)
    val precentage=bundle?.getParcelable("object") as? PrecentageData
    val movie=viewModel.listMovieLiveData.value?.get(position!!)
    val isComplated=bundle?.getBoolean("isCompleted",false)
    movie?.startDownload=!isComplated!!
    movie?.isCompleted=isComplated
    precentage?.let {
      movie?.totalFileSize=it.total
      movie?.currentDownload=it.currentPrecntage
    }
    rv.adapter?.notifyItemChanged(position!!)
  }

  val recevierFaliure=object :BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
       handleIntentFaluire(intent)
    }
  }

  private fun handleIntentFaluire(intent: Intent?) {
     val position=intent?.getIntExtra("position",0)
     val movie=viewModel.listMovieLiveData.value?.get(position!!)
     movie?.isCompleted=false
     movie?.startDownload=false
     rv.adapter?.notifyItemChanged(position!!)
     Toast.makeText(this,"failed download ${movie?.name}",Toast.LENGTH_LONG).show()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    AndroidInjection.inject(this)
    setupViews()
    setupObserve()
    viewModel.getDataAttachmentFromFakeResponse()
  }

  private fun setupObserve() {
    viewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)
    observeData()
  }

  fun registerBroadCastRecevier(){
    val bManager = LocalBroadcastManager.getInstance(this)
    val filter = IntentFilter()
    filter.addAction("PROGRESS_DOWNLOAD")
    bManager.registerReceiver(recevierSuccess,filter)
    val filter2 = IntentFilter()
    filter.addAction("FAILURE_DOWNLOAD")
    bManager.registerReceiver(recevierFaliure,filter2)
  }

  fun unregisterBroadCastRecevier(){
    unregisterReceiver(recevierFaliure)
    unregisterReceiver(recevierSuccess)
  }

  override fun onStart() {
    super.onStart()
    registerBroadCastRecevier()
  }

  override fun onStop() {
    super.onStop()
    unregisterBroadCastRecevier()
  }

  private fun setupViews() {
    rv = findViewById(R.id.rvFiles)
    progress = findViewById(R.id.progress)
    rv.layoutManager=LinearLayoutManager(this)
  }

  private fun observeData() {
    viewModel.loading.observe(this) {
      if (it) {
        progress.visibility = View.VISIBLE
      } else {
        progress.visibility = View.GONE
      }
    }
    // viewModel.notifyPosiotio.observe(this){
    //   val list=viewModel.listMovieLiveData.value
    //   Log.e("list",list.toString())
    //   rv.adapter?.notifyItemChanged(it)
    // }
    // viewModel.errrorMessage.observe(this){
    //   Toast.makeText(this,it,Toast.LENGTH_LONG).show()
    // }
    viewModel.listMovieLiveData.observe(this) {
      rv.adapter = AttachmentAdapter(it,::handleClickDownolad)
    }
  }

  private fun handleClickDownolad(movie: Movie,position:Int) {
    if (checkLocationPermission()){
      val mIntent = Intent(this, DownloadServiceUsingService::class.java)
      mIntent.putExtra("url", movie.url)
      mIntent.putExtra("NOTIFICATION_ID", movie.id)
      mIntent.putExtra("position",position)
      mIntent.putExtra(TITTLE,movie.name)
      startService(mIntent)
      //DownloadService.enqueueWork(this, mIntent,movie.id)
    }
  }

  private fun checkLocationPermission() :Boolean{
    if (VERSION.SDK_INT >= 23 && (ContextCompat.checkSelfPermission(
        this,
        permission.WRITE_EXTERNAL_STORAGE
      )
        != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(
        this,
        permission.READ_EXTERNAL_STORAGE
      )
        != PackageManager.PERMISSION_GRANTED)
    ) {
      requestSotragePermission()
      return false
    } else {
      return true
    }
  }

  private fun requestSotragePermission() {
    viewModel.compositeDisposable.add(RxPermissions(this)
      .request(
        permission.READ_EXTERNAL_STORAGE,
        permission.WRITE_EXTERNAL_STORAGE
      )
      .subscribe { granted: Boolean ->
        if (granted) {
          // start download
          Toast.makeText(this,"permission granted",Toast.LENGTH_LONG).show()
        } else {
          askStoragePermissionPopUp()
        }
      })
  }

  private fun askStoragePermissionPopUp() {
    val alertDialog=AlertDialog.Builder(this)
      .setTitle("Need permission")
      .setMessage("Must add permission storage to download attachment")
      .setPositiveButton("ok") { dialog, which ->
        requestSotragePermission()
        dialog.dismiss()
      }
      .setNegativeButton("cancel"){ dialog, which ->
        dialog.dismiss()
      }.create()
    alertDialog.show()
  }
}