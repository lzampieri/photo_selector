package com.example.photoselector.ui.main

import java.net.URLDecoder

fun parseFolderPath( folderName: String? = "" ): String {
    if( folderName == null )
        return ""
    if( folderName.isEmpty() )
        return ""
    val decFolderName = URLDecoder.decode( folderName, "UTF-8" )
    val pos = decFolderName.indexOfLast { c -> c == ':' } + 1
    if( pos > 0 )
        return decFolderName.substring( pos )
    return decFolderName
}