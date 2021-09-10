package com.example.assignmenttask.di

import com.example.assignmenttask.data.remote.ApiHelper
import com.example.assignmenttask.di.builder.ActivityBuilderModule
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import retrofit2.Retrofit
import javax.inject.Singleton

@Module(includes = [RetrofiteModule::class, ViewModleModule::class, ActivityBuilderModule::class])
class AppModule {

  @Provides fun provideCompositeDisposable(): CompositeDisposable {
    return CompositeDisposable()
  }

  @Singleton
  @Provides
  fun getApiHelper(retrofit: Retrofit): ApiHelper = retrofit.create(ApiHelper::class.java)

}