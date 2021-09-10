package com.example.assignmenttask.di.builder


import com.example.assignmenttask.service.DownloadService
import com.example.assignmenttask.ui.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilderModule {
  @ContributesAndroidInjector
  abstract fun bindMainActivity(): MainActivity

  @ContributesAndroidInjector
  abstract fun bindServiceUsingService(): DownloadService

}