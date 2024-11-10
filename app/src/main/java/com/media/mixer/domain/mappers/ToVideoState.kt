package com.media.mixer.domain.mappers

import com.media.mixer.data.entities.MediumEntity
import com.media.mixer.domain.model.VideoState

fun MediumEntity.toVideoState(): VideoState {
    return VideoState(
        path = path,
        position = playbackPosition,
        audioTrackIndex = audioTrackIndex,
        subtitleTrackIndex = subtitleTrackIndex,
        playbackSpeed = playbackSpeed
    )
}
