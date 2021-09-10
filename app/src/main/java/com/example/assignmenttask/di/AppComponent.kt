package com.example.assignmenttask.di

import android.content.Context
import com.example.assignmenttask.App
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidInjectionModule::class,AppModule::class])
interface AppComponent {
  // fun getCar():Car
  fun inject(app: App)

  @dagger.Component.Builder
  interface Builder {
    @BindsInstance fun application(application: App): Builder
    @BindsInstance fun context(context: Context?): Builder
    fun build(): AppComponent
  }
}