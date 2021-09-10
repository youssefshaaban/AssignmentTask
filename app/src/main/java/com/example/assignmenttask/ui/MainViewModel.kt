package com.example.assignmenttask.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.example.assignmenttask.data.DataRepositery
import com.example.assignmenttask.data.Movie
import com.example.assignmenttask.utils.SingleLiveEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MainViewModel @Inject constructor(
  val dataRepositery: DataRepositery,
  val compositeDisposable: CompositeDisposable
) :
  ViewModel() {
  val loading = SingleLiveEvent<Boolean>()
  val errrorMessage = SingleLiveEvent<String>()
  private val listMovieMutableLiveData = MutableLiveData<List<Movie>>()
  val listMovieLiveData: LiveData<List<Movie>> get() = listMovieMutableLiveData
  val notifyPosiotio = SingleLiveEvent<Int>()
  fun getDataAttachmentFromFakeResponse() {
    loading.value = true
    compositeDisposable.add(
      dataRepositery.getDataFromFileJson()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          loading.value = false
          listMovieMutableLiveData.value = it
        }, {
          loading.value = false
        })
    )
  }

  fun donwloadFile(position: Int, url: String, fileName: String) {
    compositeDisposable.add(
      dataRepositery.downloadTask(url, fileName).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          val item = listMovieLiveData.value?.get(position)
          item?.startDownload = true
          item?.currentDownload=it.currentPrecntage
          item?.totalFileSize=it.total
          notifyPosiotio.value=position
        }, {
          errrorMessage.value = it.message
        },{
          val item = listMovieLiveData.value?.get(position)
          item?.isCompleted=true
          item?.startDownload=false
          notifyPosiotio.value=position
        })
    )
  }

  override fun onCleared() {
    super.onCleared()
    compositeDisposable.dispose()
    compositeDisposable.clear()
  }
}