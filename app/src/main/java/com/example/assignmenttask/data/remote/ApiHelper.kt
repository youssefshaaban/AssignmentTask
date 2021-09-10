package com.example.assignmenttask.data.remote

import io.reactivex.Observable
import retrofit2.http.Url

import okhttp3.ResponseBody
import retrofit2.Response

import retrofit2.http.GET

import retrofit2.http.Streaming

interface ApiHelper {
  @Streaming
  @GET
  fun downloadFileByUrl(@Url fileUrl: String): Observable<ResponseBody>
}