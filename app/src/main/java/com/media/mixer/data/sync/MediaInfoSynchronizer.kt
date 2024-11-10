package com.media.mixer.data.sync

import android.net.Uri

interface MediaInfoSynchronizer {

    suspend fun addMedia(uri: Uri)
}
