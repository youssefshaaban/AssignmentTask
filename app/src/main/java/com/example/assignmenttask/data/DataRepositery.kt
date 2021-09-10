package com.example.assignmenttask.data

import android.os.Environment
import android.os.Parcelable
import android.util.Log
import com.example.assignmenttask.App
import com.example.assignmenttask.data.remote.ApiHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Request.Builder
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Retrofit
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.Reader
import java.lang.Exception
import javax.inject.Inject

class DataRepositery @Inject constructor(val apiHelper: ApiHelper, val okHttpClient: OkHttpClient) {

  fun getDataFromFileJson(): Observable<List<Movie>> {
    try {
      return Observable.fromCallable {
        val inputStream: InputStream = App.context.getAssets().open("getListOfFilesResponse.json")
        val gson = Gson()
        val reader: Reader = InputStreamReader(inputStream)
        val myType = object : TypeToken<List<Movie>>() {}.type
        val list: List<Movie> = gson.fromJson(reader, myType)
        list.map { movie ->
          movie.isCompleted =
            getFileToSaveOnIt(
              movie.url.substring(
                movie.url.lastIndexOf('/') + 1,
                movie.url.length
              )
            ).exists()
        }.toList()
        list
      }.subscribeOn(Schedulers.computation())
    } catch (e: IOException) {
      Log.e("Datarepo", e.toString())
    }
    return Observable.just(emptyList())
  }

  fun getFileToSaveOnIt(filename: String): File {
    val fileDir =
      File(Environment.getExternalStorageDirectory().absolutePath.toString() + "/task")
    if (!fileDir.exists()) {
      fileDir.mkdirs()
    }
    return File(fileDir, "$filename")
  }

  fun downloadTaskOkHttp2(url: String): Flowable<PrecentageData> {
    return Flowable.create<PrecentageData>({ emitter ->
      var input: InputStream? = null
      var output: OutputStream? = null
      try {
        val request: Request = Builder()
          .url(url)
          .build()
        val response = okHttpClient.newCall(request).execute()
        val file = getFileToSaveOnIt(url.substring(url.lastIndexOf('/') + 1, url.length))
        output = FileOutputStream(file)
        input = response.body()!!.byteStream()
        val tlength = response.body()!!.contentLength()
        val byteArray = ByteArray(1024)
        emitter.onNext(PrecentageData(0, tlength))
        var total: Long = 0
        var count: Int = input.read(byteArray)
        while (count != -1) {
          total += count
          emitter.onNext(PrecentageData(currentPrecntage = total, tlength))
          output.write(byteArray, 0, count)
          count = input.read(byteArray)
        }
      } catch (e: Exception) {
        emitter.onError(e)
      } finally {
        try {
          input?.close()
        } catch (ioe: IOException) {
        }
        try {
          output?.close()
        } catch (e: IOException) {
        }
      }
      emitter.onComplete()
    }, BackpressureStrategy.LATEST)
  }


  fun downloadTask(url: String, filename: String): Observable<PrecentageData> {
    return apiHelper.downloadFileByUrl(url)
      .flatMap { response ->
        Observable.create { emitter ->
          var input: InputStream? = null
          var output: OutputStream? = null
          try {
            val file = getFileToSaveOnIt(filename)
            output = FileOutputStream(file)
            input = response.byteStream()
            val tlength = response.contentLength()
            val byteArray = ByteArray(1024)
            emitter.onNext(PrecentageData(0, tlength))
            var total: Long = 0
            var count: Int = input.read(byteArray)
            while (count != -1) {
              total += count
              emitter.onNext(PrecentageData(currentPrecntage = total, tlength))
              output.write(byteArray, 0, count)
              count = input.read(byteArray)
            }
          } catch (e: Exception) {
            emitter.onError(e)
          } finally {
            try {
              input?.close()
            } catch (ioe: IOException) {
            }
            try {
              output?.close()
            } catch (e: IOException) {
            }
          }
          emitter.onComplete()
        }
      }
  }


}

@Parcelize
data class PrecentageData(val currentPrecntage: Long, val total: Long) : Parcelable