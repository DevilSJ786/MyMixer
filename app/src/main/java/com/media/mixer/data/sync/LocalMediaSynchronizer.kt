package com.media.mixer.data.sync

import android.content.ContentUris
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.media.mixer.core.utils.Audio_COLLECTION_URI
import com.media.mixer.core.utils.VIDEO_COLLECTION_URI
import com.media.mixer.core.utils.prettyName
import com.media.mixer.data.dao.DirectoryDao
import com.media.mixer.data.dao.MediumDao
import com.media.mixer.data.dao.UserDao
import com.media.mixer.data.entities.DirectoryEntity
import com.media.mixer.data.entities.MediumEntity
import com.media.mixer.media.domain.mapper.asSong
import com.media.mixer.media.domain.model.MediaAudio
import com.media.mixer.media.domain.model.MediaVideo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class LocalMediaSynchronizer @Inject constructor(
    private val mediumDao: MediumDao,
    private val directoryDao: DirectoryDao,
    private val audioDao: UserDao,
    @ApplicationContext private val context: Context,
) : MediaSynchronizer {
    private val applicationScope = CoroutineScope(SupervisorJob() +Dispatchers.Default)
    private var mediaSyncingJob: Job? = null
    private var audioSyncingJob: Job? = null

    override fun startSync() {
        if (mediaSyncingJob != null||audioSyncingJob!=null) return
        mediaSyncingJob = getMediaVideosFlow().onEach { media ->
            applicationScope.launch { updateDirectories(media) }
            applicationScope.launch { updateMedia(media) }
        }.launchIn(applicationScope)
        audioSyncingJob=getMediaAudioFlow().onEach { audio->
            applicationScope.launch { updateAudio(audio) }
        }.launchIn(applicationScope)
    }

    override fun stopSync() {
        mediaSyncingJob?.cancel()
        audioSyncingJob?.cancel()
    }

    private suspend fun updateDirectories(media: List<MediaVideo>) = withContext(
        Dispatchers.Default
    ) {
        val directories = media.groupBy { File(it.data).parentFile!! }.map { (file, _) ->
            DirectoryEntity(
                path = file.path,
                name = file.prettyName,
                modified = file.lastModified()
            )
        }
        directoryDao.upsertAll(directories)

        val currentDirectoryPaths = directories.map { it.path }

        val unwantedDirectories = directoryDao.getAll().first()
            .filterNot { it.path in currentDirectoryPaths }

        val unwantedDirectoriesPaths = unwantedDirectories.map { it.path }

        directoryDao.delete(unwantedDirectoriesPaths)
    }

    private suspend fun updateMedia(media: List<MediaVideo>) = withContext(Dispatchers.Default) {
        val mediumEntities = media.map {
            val file = File(it.data)
            val mediumEntity = mediumDao.get(it.uri.toString())
            mediumEntity?.copy(
                uriString = it.uri.toString(),
                modified = it.dateModified,
                name = file.name,
                size = it.size,
                width = it.width,
                height = it.height,
                duration = it.duration,
                mediaStoreId = it.id
            ) ?: MediumEntity(
                path = it.data,
                uriString = it.uri.toString(),
                name = file.name,
                parentPath = file.parent!!,
                modified = it.dateModified,
                size = it.size,
                width = it.width,
                height = it.height,
                duration = it.duration,
                mediaStoreId = it.id
            )
        }

        mediumDao.upsertAll(mediumEntities)

        val currentMediaUris = mediumEntities.map { it.uriString }

        val unwantedMedia = mediumDao.getAll().first()
            .filterNot { it.uriString in currentMediaUris }

        val unwantedMediaUris = unwantedMedia.map { it.uriString }

        mediumDao.delete(unwantedMediaUris)

        // Delete unwanted thumbnails
        val unwantedThumbnailFiles = unwantedMedia.mapNotNull { medium -> medium.thumbnailPath?.let { File(it) } }
        unwantedThumbnailFiles.forEach { file ->
            try {
                file.delete()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun updateAudio(media: List<MediaAudio>) = withContext(Dispatchers.Default) {
        val mediumEntities = media.map {
            val mediumEntity = audioDao.get(it.uri)
            mediumEntity ?: it.asSong()
        }

        audioDao.upsertAllAudio(mediumEntities)

        val currentMediaUris = mediumEntities.map { it.mediaUri }

        val unwantedMedia = audioDao.getPlaylistSong().first()
            .filterNot { it.mediaUri in currentMediaUris }

        val unwantedMediaUris = unwantedMedia.map { it.mediaUri }

        audioDao.delete(unwantedMediaUris)
    }
    private fun getMediaVideosFlow(
        selection: String? = null,
        selectionArgs: Array<String>? = null,
        sortOrder: String? = "${MediaStore.Video.Media.DISPLAY_NAME} ASC"
    ): Flow<List<MediaVideo>> = callbackFlow {
        val observer = object : ContentObserver(null) {
            override fun onChange(selfChange: Boolean) {
                trySend(getMediaVideo(selection, selectionArgs, sortOrder))
            }
        }
        context.contentResolver.registerContentObserver(VIDEO_COLLECTION_URI, true, observer)
        // initial value
        trySend(getMediaVideo(selection, selectionArgs, sortOrder))
        // close
        awaitClose { context.contentResolver.unregisterContentObserver(observer) }
    }.flowOn(Dispatchers.IO).distinctUntilChanged()

    private fun getMediaVideo(
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): List<MediaVideo> {
        val mediaVideos = mutableListOf<MediaVideo>()
        context.contentResolver.query(
            VIDEO_COLLECTION_URI,
            VIDEO_PROJECTION,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->

            val idColumn = cursor.getColumnIndex(MediaStore.Video.Media._ID)
            val dataColumn = cursor.getColumnIndex(MediaStore.Video.Media.DATA)
            val durationColumn = cursor.getColumnIndex(MediaStore.Video.Media.DURATION)
            val widthColumn = cursor.getColumnIndex(MediaStore.Video.Media.WIDTH)
            val heightColumn = cursor.getColumnIndex(MediaStore.Video.Media.HEIGHT)
            val sizeColumn = cursor.getColumnIndex(MediaStore.Video.Media.SIZE)
            val dateModifiedColumn = cursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                mediaVideos.add(
                    MediaVideo(
                        id = id,
                        data = cursor.getString(dataColumn),
                        duration = cursor.getLong(durationColumn),
                        uri = ContentUris.withAppendedId(VIDEO_COLLECTION_URI, id),
                        width = cursor.getInt(widthColumn),
                        height = cursor.getInt(heightColumn),
                        size = cursor.getLong(sizeColumn),
                        dateModified = cursor.getLong(dateModifiedColumn)
                    )
                )
            }
        }
        return mediaVideos.filter { File(it.data).exists() }
    }

   private fun getMediaAudioFlow(
        selection: String? = null,
        selectionArgs: Array<String>? = null,
        sortOrder: String? = "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"
    ): Flow<List<MediaAudio>> = callbackFlow {
        val observer = object : ContentObserver(null) {
            override fun onChange(selfChange: Boolean) {
                trySend(getMediaAudio(context, selection, selectionArgs, sortOrder))
            }
        }
        context.contentResolver.registerContentObserver(Audio_COLLECTION_URI, true, observer)
        // initial value
        trySend(getMediaAudio(context, selection, selectionArgs, sortOrder))
        // close
        awaitClose { context.contentResolver.unregisterContentObserver(observer) }
    }.flowOn(Dispatchers.IO).distinctUntilChanged()

    private fun getMediaAudio(
        context: Context,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): List<MediaAudio> {
        val mediaAudios = mutableListOf<MediaAudio>()
        context.contentResolver.query(
            Audio_COLLECTION_URI,
            Audio_PROJECTION,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->

            val idColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val dataColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val durationColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
            val titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val albumIdColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
            val albumNameColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)
            val sizeColumn = cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)
            val typeColumn = cursor.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE)


            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val data = cursor.getString(dataColumn)
                val duration = cursor.getLong(durationColumn)
                val size = cursor.getString(sizeColumn)?:"0"
                val title = cursor.getString(titleColumn)
                val type = cursor.getString(typeColumn)
                val artist = cursor.getString(artistColumn)
                val albumId = cursor.getLong(albumIdColumn)
                val albumName = cursor.getString(albumNameColumn)
                val uri = ContentUris.withAppendedId(Audio_COLLECTION_URI, id)
                val sArt = Uri.parse("content://media/external/audio/albumart")
                val artUri = ContentUris.withAppendedId(sArt, albumId)
                Log.d("TAG", "getMediaAudio:arturi $artUri,data $data")
                Log.d("TAG", "getMediaAudio:album $albumName $albumId")

                mediaAudios.add(
                    MediaAudio(
                        id = id,
                        data = data,
                        duration = duration,
                        size = size,
                        uri = uri,
                        title = title,
                        artist = artist,
                        art = artUri,
                        albumId = albumId,
                        albumName = albumName,
                        isFev = false,
                        type = type
                    )
                )
            }
        }

        return mediaAudios.filter { File(it.data).exists() }
    }
    companion object {
        val VIDEO_PROJECTION = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.HEIGHT,
            MediaStore.Video.Media.WIDTH,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATE_MODIFIED
        )
        val Audio_PROJECTION = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.MIME_TYPE
        )
    }
}
