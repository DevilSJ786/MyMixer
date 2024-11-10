package com.media.mixer.media.domain.mapper

import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import androidx.media3.common.MediaItem
import com.media.mixer.data.entities.Song
import com.media.mixer.media.domain.model.MediaAudio
import com.media.mixer.media.domain.utils.MediaConstants.DEFAULT_MEDIA_ID
import com.media.mixer.media.domain.utils.buildPlayableMediaItem


fun Song.asMediaItem() = buildPlayableMediaItem(
    mediaId = id.toString(),
    mediaUri = mediaUri,
    artworkUri = artworkUri,
    title = title,
    artist = artist
)
fun DocumentFile.toSong()=Song(
    id = -1,
    mediaUri = this.uri,
    artworkUri =Uri.EMPTY ,
    title = this.name?.replace(".mp3","").orEmpty(),
    artist = this.name.orEmpty(),
    duration = 0,
    album = " ",
    size =this.length().toString() ,
    isFev = false,
    path = this.uri.toString().replace("file://",""),
    type = this.type.orEmpty()
)

internal fun MediaItem?.asSong() = Song(
    id = this?.mediaId?.toInt() ?: DEFAULT_MEDIA_ID,
    mediaUri = this?.requestMetadata?.mediaUri.orEmpty(),
    artworkUri = this?.mediaMetadata?.artworkUri.orEmpty(),
    title = this?.mediaMetadata?.title.orEmpty(),
    artist = this?.mediaMetadata?.artist.orEmpty(),
    duration = 0,
    album = " ",
    size ="" ,
    isFev = false,
    path = "",
    type = ""
)
fun MediaAudio.asSong()= Song(
    id = this.id.toInt() ,
    mediaUri = this.uri,
    artworkUri = this.art,
    title = this.title,
    artist = this.artist,
    duration = this.duration.toInt(),
    album=this.albumName,
    size = this.size,
    isFev = this.isFev,
    path=this.data,
    type=this.type
)

private fun Uri?.orEmpty() = this ?: Uri.EMPTY
private fun CharSequence?.orEmpty() = (this ?: "").toString()
