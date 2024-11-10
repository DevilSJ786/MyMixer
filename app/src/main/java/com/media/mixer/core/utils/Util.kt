package com.media.mixer.core.utils

import androidx.media3.common.MimeTypes

object Util {
    const val SHOULD_REMOVE_AUDIO = "should_remove_audio"
    const val SHOULD_REMOVE_VIDEO = "should_remove_video"
    const val SHOULD_FLATTEN_FOR_SLOW_MOTION = "should_flatten_for_slow_motion"
    const val FORCE_AUDIO_TRACK = "force_audio_track"
    const val AUDIO_MIME_TYPE = "audio_mime_type"
    const val VIDEO_MIME_TYPE = "video_mime_type"
    const val RESOLUTION_HEIGHT = "resolution_height"
    const val SCALE_X = "scale_x"
    const val SCALE_Y = "scale_y"
    const val ROTATE_DEGREES = "rotate_degrees"
    const val TRIM_START_MS = "trim_start_ms"
    const val TRIM_END_MS = "trim_end_ms"
    const val ENABLE_FALLBACK = "enable_fallback"
    const val ABORT_SLOW_EXPORT = "abort_slow_export"
    const val PRODUCE_FRAGMENTED_MP4 = "produce_fragmented_mp4"
    const val HDR_MODE = "hdr_mode"
    const val AUDIO_EFFECTS_SELECTIONS = "audio_effects_selections"
    const val VIDEO_EFFECTS_SELECTIONS = "video_effects_selections"
    const val COLOR_FILTER_SELECTION = "color_filter_selection"
    const val CONTRAST_VALUE = "contrast_value"
    const val RGB_ADJUSTMENT_RED_SCALE = "rgb_adjustment_red_scale"
    const val RGB_ADJUSTMENT_GREEN_SCALE = "rgb_adjustment_green_scale"
    const val RGB_ADJUSTMENT_BLUE_SCALE = "rgb_adjustment_blue_scale"
    const val HSL_ADJUSTMENTS_HUE = "hsl_adjustments_hue"
    const val HSL_ADJUSTMENTS_SATURATION = "hsl_adjustments_saturation"
    const val HSL_ADJUSTMENTS_LIGHTNESS = "hsl_adjustments_lightness"

    // Video effect selections.
    const val COLOR_FILTERS_INDEX = 2
    const val MAP_WHITE_TO_GREEN_LUT_INDEX = 3
    const val RGB_ADJUSTMENTS_INDEX = 4
    const val HSL_ADJUSTMENT_INDEX = 5
    const val CONTRAST_INDEX = 6

    // Audio effect selections.
    const val HIGH_PITCHED_INDEX = 0
    const val SAMPLE_RATE_INDEX = 1
    const val SKIP_SILENCE_INDEX = 2
    const val CHANNEL_MIXING_INDEX = 3
    const val VOLUME_SCALING_INDEX = 4


    // Color filter options.
    const val COLOR_FILTER_GRAYSCALE = 0
    const val COLOR_FILTER_INVERTED = 1
    const val COLOR_FILTER_SEPIA = 2

    const val SAME_AS_INPUT_OPTION = "same as input"

}