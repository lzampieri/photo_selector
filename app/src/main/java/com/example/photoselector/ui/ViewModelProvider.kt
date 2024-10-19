package com.example.photoselector.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.photoselector.PhotoselectorApplication
import com.example.photoselector.ui.main.MainViewModel

/**
 * Provides Factory to create instance of ViewModel for the entire Inventory app
 */
object ViewModelProvider {
    val Factory = viewModelFactory {
        // Initializer for MainViewModel
        initializer {
            MainViewModel(
                theApp().applicationContext,
                theApp().container.imagesRepository
            )
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [InventoryApplication].
 */
fun CreationExtras.theApp(): PhotoselectorApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as PhotoselectorApplication)