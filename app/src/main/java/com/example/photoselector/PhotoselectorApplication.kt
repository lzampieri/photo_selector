package com.example.photoselector

import android.app.Application
import com.example.photoselector.data.ActualAppContainer
import com.example.photoselector.data.AppContainer

class PhotoselectorApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = ActualAppContainer(this)
    }
}