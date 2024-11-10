package com.media.mixer.media.domain.utils

import androidx.annotation.DrawableRes
import com.media.mixer.R
import com.media.mixer.media.domain.utils.Icon.DrawableResourceIcon

object AppIcons {
    val Music = DrawableResourceIcon(R.drawable.music)
    val SkipPrevious = DrawableResourceIcon(R.drawable.ic_skip_previous)
    val Play = DrawableResourceIcon(R.drawable.ic_play)
    val Pause = DrawableResourceIcon(R.drawable.ic_pause)
    val SkipNext = DrawableResourceIcon(R.drawable.ic_skip_next)
    val SkipForward = DrawableResourceIcon(R.drawable.fordward)
    val SkipBack = DrawableResourceIcon(R.drawable.fordward)
}

sealed interface Icon {
    data class DrawableResourceIcon(@DrawableRes val resourceId: Int) : Icon
}
