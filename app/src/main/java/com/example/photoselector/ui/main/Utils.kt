package com.example.photoselector.ui.main

import android.util.Log
import java.net.URLDecoder
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

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