package com.example.assignmenttask.di

import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.Retrofit.Builder
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.security.KeyManagementException
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.util.concurrent.TimeUnit.SECONDS
import javax.inject.Singleton

@Module
class RetrofiteModule {

  @Singleton
  @Provides
  fun getOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
    .readTimeout(120, SECONDS)
    .connectTimeout(120, SECONDS)
    .build()

  /**
   * @return builder call for retrofit  with return of retrofit instance
   */
  @Singleton
  @Provides
   fun getRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Builder()
      .addConverterFactory(GsonConverterFactory.create())
      .callFactory(okHttpClient)
      .baseUrl("https://nagwa.free.beeceptor.com/")
      .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
      .build()
  }
}