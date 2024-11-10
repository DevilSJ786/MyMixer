package com.media.mixer.media.domain.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.media.mixer.R
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun Long.asFormattedString() = milliseconds.toComponents { minutes, seconds, _ ->
    stringResource(
        id = R.string.player_timestamp_format,
        String.format(locale = Locale.US, format = "%02d", minutes),
        String.format(locale = Locale.US, format = "%02d", seconds)
    )
}
fun convertToPosition(value: Float, total: Long) = (value * total).toLong()
