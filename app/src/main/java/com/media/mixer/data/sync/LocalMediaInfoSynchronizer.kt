package com.media.mixer.data.sync

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.media.mixer.core.utils.thumbnailCacheDir
import com.media.mixer.data.dao.MediumDao
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.anilbeesetti.nextlib.mediainfo.MediaInfoBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class LocalMediaInfoSynchronizer @Inject constructor(
    private val mediumDao: MediumDao,
    @ApplicationContext private val context: Context,
) : MediaInfoSynchronizer {
   private val applicationScope =CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val media = MutableSharedFlow<Uri>()

    override suspend fun addMedia(uri: Uri) = media.emit(uri)

    private suspend fun sync(): Unit = withContext(Dispatchers.Default) {
        media.collect { mediumUri ->
            val medium = mediumDao.getWithInfo(mediumUri.toString()) ?: return@collect

            if (medium.thumbnailPath?.let { File(it) }?.exists() == true) {
                return@collect
            }


            val mediaInfo = try {
                MediaInfoBuilder().from(context,mediumUri).build() ?: throw NullPointerException()
            } catch (e: Exception) {
                Log.d(TAG, "sync: MediaInfoBuilder exception", e)
                return@collect
            }

            val thumbnail = mediaInfo.getFrame()
            mediaInfo.release()

            val thumbnailPath =
                thumbnail?.saveTo(storageDir = context.thumbnailCacheDir, quality = 30)

            mediumDao.upsert(
                medium.copy(
                    format = mediaInfo.format,
                    thumbnailPath = thumbnailPath
                )
            )
        }
    }

    init {
        applicationScope.launch { sync() }
    }

    companion object {
        private const val TAG = "MediaInfoSynchronizer"
    }
}


suspend fun Bitmap.saveTo(storageDir: File, quality: Int = 100): String? =
    withContext(Dispatchers.IO) {
        val thumbnailFileName = "thumbnail-${System.currentTimeMillis()}"
        val thumbFile = File(storageDir, thumbnailFileName)
        try {
            FileOutputStream(thumbFile).use { fos ->
                compress(Bitmap.CompressFormat.JPEG, quality, fos)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext if (thumbFile.exists()) thumbFile.path else null
    }
