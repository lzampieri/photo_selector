package com.example.photoselector.ui.models

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoselector.R
import com.example.photoselector.data.Action
import com.example.photoselector.data.AppContainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

val ActionIcons = arrayOf(
    R.drawable.baseline_conveyor_belt_24,
    R.drawable.outline_add_photo_alternate_24,
    R.drawable.outline_add_to_drive_24,
    R.drawable.outline_app_registration_24,
    R.drawable.outline_cable_24,
    R.drawable.outline_cloud_upload_24,
    R.drawable.outline_delete_24,
    R.drawable.outline_email_24,
    R.drawable.outline_fireplace_24,
    R.drawable.outline_folder_open_24,
    R.drawable.outline_next_week_24,
    R.drawable.outline_print_24
)

class ActionsViewModel (
    public val appContainer: AppContainer,
) : ViewModel() {

    val name = MutableStateFlow("")
    val copy = MutableStateFlow(false)
    val icon = MutableStateFlow(0)
    val path = MutableStateFlow("")

    fun setFolder(uri: Uri ) {
        appContainer.appContext.contentResolver.takePersistableUriPermission(
            uri, Intent.FLAG_GRANT_READ_URI_PERMISSION + Intent.FLAG_GRANT_WRITE_URI_PERMISSION )

        path.value = uri.toString()
    }

    fun save() {
        viewModelScope.launch(Dispatchers.IO) {
            addActionAct( name.value, copy.value, icon.value, path.value )
            name.value = ""
            copy.value = false
            icon.value = 0
            path.value = ""
        }
    }

    private suspend fun addActionAct( name: String, copy: Boolean, icon: Int, path: String ) {
        appContainer.repository.addAction( name, copy, icon, path )
    }

    fun deleteAction( action: Action ) {
        viewModelScope.launch(Dispatchers.IO) {
            appContainer.repository.hideAction( action )
        }
    }
}