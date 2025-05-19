package com.example.photoselector.ui.models

import android.R.id
import android.app.Application
import android.content.ContentUris
import android.content.Intent
import android.database.Cursor
import android.icu.util.LocaleData
import android.media.ExifInterface
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.DocumentsContract
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoselector.PhotoselectorApplication
import com.example.photoselector.data.FolderAndCounts
import com.example.photoselector.data.Image
import com.example.photoselector.data.ImageAndAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import java.io.FileDescriptor
import java.nio.file.Files.isDirectory
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import java.util.LinkedList
import java.util.Locale


class AppViewModel (
    val app: Application,
) : AndroidViewModel(app) {

    val appContainer = (app as PhotoselectorApplication).container

    val folders : Flow<List<FolderAndCounts>> = appContainer.repository.getAllFolders()
    val selectedFolder: MutableStateFlow<FolderAndCounts?> = MutableStateFlow( null )
    val loading = MutableStateFlow( 0 )
    val imagesDbLoading = MutableStateFlow( 0 )

    val images: Flow<List<ImageAndAction>>
        get() = this.appContainer.repository.getImagesAndActionsFromFolder( selectedFolder.value?.id ?: -1 )

    init {
        viewModelScope.launch(Dispatchers.IO) {
            refreshFoldersContent()
        }
    }

    fun selectFolder( folderId: Int ) {
        if( folderId == selectedFolder.value?.id ) return
        loading.value += 1
        viewModelScope.launch(Dispatchers.IO) {
            selectedFolder.value = appContainer.repository.getFolder( folderId )
            loading.value -= 1
        }
    }

    fun deleteSelectedFolder() {
        if( selectedFolder.value == null ) return
        loading.value += 1
        viewModelScope.launch(Dispatchers.IO) {
            appContainer.repository.deleteFolder( selectedFolder.value!! )
            loading.value -= 1
        }
    }

    fun addFolder( uri: Uri ) {
        viewModelScope.launch(Dispatchers.IO) {
            addFolderAct( uri )
        }
    }

    fun refresh( hard: Boolean = false ) {
        viewModelScope.launch(Dispatchers.IO) {
            if( hard )
                appContainer.repository.deleteAllImages()
            refreshFoldersContent()
        }
    }

    private suspend fun addFolderAct(uri: Uri ) {
        appContainer.appContext.contentResolver.takePersistableUriPermission(
            uri, Intent.FLAG_GRANT_READ_URI_PERMISSION + Intent.FLAG_GRANT_WRITE_URI_PERMISSION )

        appContainer.repository.addFolderIfNotExists(
            uri.toString()
        )
        refreshFoldersContent()
    }

    private fun parseDateFromFilename(folderName: String, default: Long ): Long {
        val regex = Regex("(20\\d\\d[01]\\d[0123]\\d)[^\\d]")
        val matches = regex.findAll( folderName )
        if( matches.count() > 0 ) {
            matches.forEach { m ->
                try {
                    val date = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).parse(m.groupValues[1])
                    return (date?.time ?: 0).toLong()
                } catch ( _: ParseException) {}
            }
        }
        return default
    }

    private suspend fun refreshFoldersContent() {
        imagesDbLoading.value += 1

        val fl: List<FolderAndCounts> = appContainer.repository.getAllFolders().take(1).last()

        // appContainer.repository.deleteUnactionedImages()

        fl.forEach { ff ->
            try {
                // Prepare scanning structure
                val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(
                    Uri.parse( ff.path ),
                    DocumentsContract.getTreeDocumentId( Uri.parse( ff.path ) )
                )
                val diskImages: MutableList<Image> = mutableListOf<Image>()

                // Define cursor
                val c: Cursor? = appContainer.appContext.contentResolver.query(
                    childrenUri,
                    arrayOf<String>(
                        DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                        DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                        DocumentsContract.Document.COLUMN_MIME_TYPE,
                        DocumentsContract.Document.COLUMN_LAST_MODIFIED
                    ),
                    null, // KNOWN TO NOT WORK
                    null, // KNOWN TO NOT WORK
                    null // KNOWN TO NOT WORK
                )
                if( c == null )
                    return@forEach

                // Add images to list
                while (c.moveToNext()) {
                    val mime = c.getString(2)
                    if( mime.startsWith("image") ) {
                        diskImages.add( Image(
                            folderId = ff.id,
                            name = c.getString(1),
                            path = DocumentsContract.buildDocumentUriUsingTree(
                                Uri.parse( ff.path ),
                                c.getString(0)
                            ).toString(),
                            actionId = null
                        ) )
                    }
                }

                c.close()
                
                // Sort
                diskImages.sortBy { it.path }

                // Get the actual list of images
                val imgs = appContainer.repository.getImagesFromFolder( ff.id ).take(1).last()
                val dbImages = imgs.sortedBy { it.path }

                val imgsToAdd: MutableList<Image> = mutableListOf<Image>()
                val imgsToRemove: MutableList<Int> = mutableListOf<Int>()

                var diskImagesI = 0;
                var dbImagesI = 0;
                while(( diskImagesI < diskImages.size ) || ( dbImagesI < dbImages.size ) ) {

                    if( diskImagesI == diskImages.size ) {
                        // Images on the disk are done, but on the DB there is still something
                        imgsToRemove.add( dbImages[ dbImagesI ].id );
                        dbImagesI += 1;
                        continue;
                    }

                    if( dbImagesI == dbImages.size ) {
                        // Images on the db are done, but on the disk there is still something
                        imgsToAdd.add( diskImages[ diskImagesI ] );
                        diskImagesI += 1;
                        continue;
                    }

                    if( diskImages[diskImagesI].path == dbImages[dbImagesI].path ) {
                        // Same image, all fine
                        diskImagesI += 1;
                        dbImagesI += 1;
                        continue;
                    }

                    if( diskImages[ diskImagesI ].path < dbImages[ dbImagesI ].path ) {
                        // There is an image on the disk which is not on the DB!
                        imgsToAdd.add( diskImages[ diskImagesI ] );
                        diskImagesI += 1;
                        continue;
                    }


                    if( dbImages[ dbImagesI ].path < diskImages[ diskImagesI ].path ) {
                        // There is an image on the db which is not on the disk!
                        imgsToRemove.add( dbImages[ dbImagesI ].id );
                        dbImagesI += 1;
                        continue;
                    }
                }

                appContainer.repository.addImages( imgsToAdd );
                appContainer.repository.deleteImages( imgsToRemove );

                // OLD VERSION DISMISSED
                // Compare with actual list of images
//                imgs.forEach externalForEach@{ img ->
//                    childrenImages.forEach internalForEach@{ ci ->
//                        if( ci.path == img.path ) {
//                            // The new detected image is already in the list! Not to add
//                            ci.toAdd = false
//                            return@externalForEach // Continue to the next image
//                        }
//                    }
//                    // If reaching here, it means that "img" does not exists anymore!
//                    Log.d("TEST", "Deleting image " + img.name)
//                    appContainer.repository.deleteImage( img )
//                }
//
//                childrenImages.forEach{ ci ->
//                    if( ci.toAdd ) {
//                        appContainer.repository.addImage(ff.id, ci.path, ci.name)
//                        Log.d(
//                            "TEST",
//                            "Adding image " + ci.name + " - " + SimpleDateFormat("dd/MM/yyyy").format(
//                                ci.lastModified
//                            )
//                        )
//                    }
//                }


            } catch ( e: IllegalArgumentException ) {
                // appContainer.imagesRepository.deleteFolder( ff ) // TODO gestire
            }
        }

        imagesDbLoading.value -= 1
    }

    public fun runActions() {
        viewModelScope.launch(Dispatchers.IO) {
            loading.value += 1

            val imgs = images.take(1).last()

            imgs.forEach { img ->
                if( img.image.actionDone ) return@forEach
                if( img.action == null ) return@forEach

                if( img.action.type == "skip" ) {
                    setActionDone( img )
                    return@forEach
                }


                if( img.action.type == "copy" ) {
                    filesCopy( img.image.path, img.action.path!! )
                    setActionDone( img )
                    return@forEach
                }


                if( img.action.type == "move" ) {
                    filesCopy( img.image.path, img.action.path!! )
                    fileDelete( img.image.path )
                    appContainer.repository.deleteImage( img.image )
                    return@forEach
                }
            }

            refreshFoldersContent()

            loading.value -= 1
        }
    }

    private suspend fun filesCopy( from: String, to: String ) {
        val fromFile : DocumentFile = DocumentFile.fromTreeUri( appContainer.appContext, Uri.parse( from ) )!!
        val toDir : DocumentFile = DocumentFile.fromTreeUri( appContainer.appContext, Uri.parse( to ) )!!

        val newFile: DocumentFile = toDir.createFile( fromFile.type!!, fromFile.name!! )!!

        val inp = appContainer.appContext.contentResolver.openInputStream( fromFile.uri )
        val out = appContainer.appContext.contentResolver.openOutputStream( newFile.uri )

        val buffer = ByteArray(1024)
        var read: Int
        while ( (inp?.read(buffer).also { read = it!! }) != -1) {
            out?.write(buffer, 0, read)
        }
        inp?.close()

        // write the output file (You have now copied the file)
        out?.flush()
        out?.close()

        val inp_2 = appContainer.appContext.contentResolver.openInputStream( fromFile.uri )
        if( inp_2 != null ) {
            val inp_exif = ExifInterface(inp_2)
            val dateTime: String = inp_exif.getAttribute( ExifInterface.TAG_DATETIME ) ?:
                SimpleDateFormat("yyyy:MM:dd hh:mm:ss", Locale.getDefault()).format(
                    parseDateFromFilename( fromFile.name ?: "", Instant.now().toEpochMilli() )
                )

            val fileDescriptor: ParcelFileDescriptor? = appContainer.appContext.contentResolver
                .openFileDescriptor( newFile.uri, "rw" )

            if( fileDescriptor != null ) {
                val outExif = ExifInterface( fileDescriptor.fileDescriptor )
                outExif.setAttribute( ExifInterface.TAG_DATETIME, dateTime )
                outExif.saveAttributes()
            } else {
                Log.d("TEST","Error in file descriptor")
            }

            fileDescriptor?.close()
        }
        inp_2?.close()

    }

    private suspend fun fileDelete( from: String ) {
        val fromFile: DocumentFile =
            DocumentFile.fromTreeUri(appContainer.appContext, Uri.parse(from))!!
        fromFile.delete()
    }

    private suspend fun setActionDone( iaa: ImageAndAction ) {
        iaa.image.actionDone = true
        appContainer.repository.updateImage( iaa.image )
    }
}