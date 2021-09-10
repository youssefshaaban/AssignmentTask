package com.example.assignmenttask

import android.app.Application
import android.content.Context
import com.example.assignmenttask.di.AppComponent
import com.example.assignmenttask.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class App : Application(), HasAndroidInjector {
  @Inject lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

  override fun onCreate() {
    super.onCreate()
    initDagger()
    context=applicationContext
  }

  private fun initDagger() {
    val appComponent: AppComponent = DaggerAppComponent.builder()
      .application(this)
      .context(this.applicationContext)
      .build()
    appComponent.inject(this)
  }

  companion object{
    lateinit var context: Context
  }
  override fun androidInjector(): AndroidInjector<Any> {
    return dispatchingAndroidInjector
  }
}