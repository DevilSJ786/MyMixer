package com.media.mixer.core.utils

import android.content.ContentUris
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.OptIn
import androidx.documentfile.provider.DocumentFile
import androidx.media3.common.util.Clock
import androidx.media3.common.util.UnstableApi
import com.media.mixer.media.domain.model.MediaAudio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import java.io.File


@OptIn(UnstableApi::class)
object StorageHelper {

    fun getOutputFileForAudio(context: Context, extension: String, targetFolder: String): String {
        val currentTime = Clock.DEFAULT.elapsedRealtime()
        val dir = context.getExternalFilesDir(null) ?: context.filesDir
        val myDir = File("$dir/$targetFolder")
        if (!myDir.exists()) {
            myDir.mkdirs()
        }
        return myDir.absolutePath + "/output$currentTime.$extension"
    }

    fun getOutputDir(context: Context, targetFolder: String): DocumentFile {
        val dir = context.getExternalFilesDir(null) ?: context.filesDir
        val myDir = File("$dir/$targetFolder")
        return DocumentFile.fromFile(myDir)
    }

}
