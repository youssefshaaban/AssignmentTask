package com.example.assignmenttask.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.assignmenttask.ui.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModleModule {
  @Binds
  @IntoMap
  @ViewModelKey( MainViewModel::class )
  // Bind your View Model here
  abstract fun bindMainViewModel( mainViewModel: MainViewModel ): ViewModel
  @Binds
  abstract fun bindViewModelFactory( factory: ViewModelFactory):
    ViewModelProvider.Factory
}