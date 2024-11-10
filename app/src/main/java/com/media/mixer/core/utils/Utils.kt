package com.media.mixer.core.utils

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import androidx.media3.common.C
import androidx.media3.common.Effect
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaItem.ClippingConfiguration
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.audio.ChannelMixingAudioProcessor
import androidx.media3.common.audio.ChannelMixingMatrix
import androidx.media3.common.audio.SonicAudioProcessor
import androidx.media3.common.util.UnstableApi
import androidx.media3.effect.Contrast
import androidx.media3.effect.HslAdjustment
import androidx.media3.effect.Presentation
import androidx.media3.effect.RgbAdjustment
import androidx.media3.effect.RgbFilter
import androidx.media3.effect.RgbMatrix
import androidx.media3.effect.ScaleAndRotateTransformation
import androidx.media3.effect.SingleColorLut
import androidx.media3.exoplayer.audio.SilenceSkippingAudioProcessor
import androidx.media3.transformer.Composition
import androidx.media3.transformer.DefaultEncoderFactory
import androidx.media3.transformer.DefaultMuxer
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.EditedMediaItemSequence
import androidx.media3.transformer.Effects
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.InAppMuxer
import androidx.media3.transformer.Muxer
import androidx.media3.transformer.Transformer
import com.google.common.collect.ImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.Arrays
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.abs

val Audio_COLLECTION_URI: Uri
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
    } else {
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    }
val VIDEO_COLLECTION_URI: Uri
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
    } else {
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    }

fun getAvailableFiles(context: Context, folderName: String): List<DocumentFile> {
    val data = StorageHelper.getOutputDir(context, folderName).listFiles()
    return data.filter { it.isFile }.toList()
}

fun encode(url: String) = URLEncoder.encode(url, "UTF-8")
fun decode(url: String) = URLDecoder.decode(url, "UTF-8")
fun createMediaItem(bundle: Bundle?, uri: String): MediaItem {
    val mediaItemBuilder = MediaItem.Builder().setUri(uri)
    Log.d("TAG", "createMediaItem:uri $uri")
    if (bundle != null) {
        val trimStartMs = bundle.getLong(
            Util.TRIM_START_MS,
            C.TIME_UNSET
        )
        val trimEndMs =
            bundle.getLong(Util.TRIM_END_MS, C.TIME_UNSET)
        if (trimStartMs != C.TIME_UNSET && trimEndMs != C.TIME_UNSET) {
            mediaItemBuilder.setClippingConfiguration(
                ClippingConfiguration.Builder()
                    .setStartPositionMs(trimStartMs)
                    .setEndPositionMs(trimEndMs)
                    .build()
            )
        }
    }
    return mediaItemBuilder.build()
}

fun createMediaItemForTrim(trimItem: TrimItem): MediaItem {
    val newUri =
        if (trimItem.uri.contains("storage/emulated") && !trimItem.uri.contains("file://")) {
            Uri.fromFile(File(trimItem.uri))
        } else Uri.parse(trimItem.uri)
    val mediaItemBuilder = MediaItem.Builder().setUri(newUri)
    mediaItemBuilder.setClippingConfiguration(
        ClippingConfiguration.Builder()
            .setStartPositionMs(trimItem.start)
            .setEndPositionMs(trimItem.end)
            .build()
    )
    return mediaItemBuilder.build()
}

@OptIn(UnstableApi::class)
fun createTransformer(
    bundle: Bundle?,
    context: Context,
    onCompletion: () -> Unit,
    onException: (String) -> Unit,
): Transformer {
    val transformerBuilder = Transformer.Builder(context)
    if (bundle != null) {
        val audioMimeType = bundle.getString(Util.AUDIO_MIME_TYPE)
        Log.d("TAG", "createTransformer: audiomemetype:$audioMimeType")
        if (audioMimeType != null) {
            transformerBuilder.setAudioMimeType(audioMimeType)
        }
        val videoMimeType = bundle.getString(Util.VIDEO_MIME_TYPE)
        if (videoMimeType != null) {
            transformerBuilder.setVideoMimeType(videoMimeType)
        }


        val encoderFactory = DefaultEncoderFactory.Builder(context.applicationContext).apply {
            setEnableFallback(bundle.getBoolean(Util.ENABLE_FALLBACK))
        }.build()
        transformerBuilder.setEncoderFactory(
            encoderFactory
        )
        var maxDelayBetweenSamplesMs = DefaultMuxer.Factory.DEFAULT_MAX_DELAY_BETWEEN_SAMPLES_MS
        if (!bundle.getBoolean(Util.ABORT_SLOW_EXPORT)) {
            maxDelayBetweenSamplesMs = C.TIME_UNSET
        }
        var muxerFactory: Muxer.Factory = DefaultMuxer.Factory(maxDelayBetweenSamplesMs)
        if (bundle.getBoolean(Util.PRODUCE_FRAGMENTED_MP4)) {
            muxerFactory = InAppMuxer.Factory.Builder()
                .setMaxDelayBetweenSamplesMs(maxDelayBetweenSamplesMs)
                .setFragmentedMp4Enabled(true)
                .build()
        }
        transformerBuilder.setMuxerFactory(muxerFactory)

    }
    return transformerBuilder
        .addListener(
            object : Transformer.Listener {
                override fun onCompleted(composition: Composition, exportResult: ExportResult) {
                    onCompletion()
                }

                override fun onError(
                    composition: Composition,
                    exportResult: ExportResult,
                    exportException: ExportException
                ) {
                    onException(exportException.localizedMessage ?: "error")
                    Log.d("TAG", "onError: ${exportException.localizedMessage}")
                }
            })
        .build()
}

fun setTone(
    context: Context,
    isRingtone: Boolean = false,
    isNotification: Boolean = false,
    isAlarm: Boolean = false,
    sourcePath: String
) {

    val sourceFile = File(sourcePath)
    val fileStorage =
        if (isRingtone) StoragePath.RINGTONE.path else if (isNotification) StoragePath.NOTIFICATIONS.path else StoragePath.ALARM.path

    if (sourceFile.exists() && sourceFile.isFile) {
        val destinationFile = createExternalFile(
            fileName = sourceFile.name,
            context = context,
            fileStorage = fileStorage
        )
        copyFile(sourceFile, destinationFile)
    } else {
        Log.e("TAG", "setTone: Source file or destination folder doesn't exist.")
    }
    setRingtoneManageTone(
        context = context,
        path = sourcePath,
        isRingtone = isRingtone,
        isAlarm = isAlarm,
        isNotification = isNotification
    )
}

fun createExternalFile(fileName: String, fileStorage: String, context: Context): File {
    val dir = context.getExternalFilesDir(null) ?: context.filesDir
    val myDir = File("$dir/$fileStorage")
    if (!myDir.exists()) {
        myDir.mkdirs()
    }
    return File(myDir, fileName)
}

fun copyFile(sourceFile: File, destinationFile: File) {
    try {
        val inputStream = FileInputStream(sourceFile)
        val outputStream = FileOutputStream(destinationFile)
        val buffer = ByteArray(1024)
        var length: Int
        while (inputStream.read(buffer).also { length = it } > 0) {
            outputStream.write(buffer, 0, length)
        }
        inputStream.close()
        outputStream.close()
        Log.i("TAG", "copyFile: File copied successfully. ${destinationFile.absolutePath}")
    } catch (e: IOException) {
        e.printStackTrace()
        Log.e("TAG", "copyFile: ${e.localizedMessage}")
    }
}

fun viewContent(path: String, context: Context) {
    val intent = Intent(Intent.ACTION_VIEW)
    val uri = Uri.parse(path)
    val downloadedFile = DocumentFile.fromSingleUri(context, uri)!!
    if (!downloadedFile.exists()) {
        Toast.makeText(context, "file not found", Toast.LENGTH_SHORT).show()
        return
    }
    val mimeType = context.contentResolver.getType(uri) ?: "*/*"
    intent.setDataAndType(uri, mimeType)
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    if (intent.resolveActivity(context.packageManager) != null) {
        ContextCompat.startActivity(context, intent, null)
    } else {
        Toast.makeText(context, "No app found to view this file.", Toast.LENGTH_SHORT).show()
    }
}

fun getFolderPermissionIntent() = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
            Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
}

const val DefaultSaveLocationKey = "default_save_location"
suspend fun saveAs(
    context: Context,
    saveLocation: String,
    fileName: String,
    filePath: String,
    mimeType: String
) {
    val treeUri = Uri.parse(saveLocation)
    val docId = DocumentsContract.getTreeDocumentId(treeUri)
    val destDir = DocumentsContract.buildDocumentUriUsingTree(treeUri, docId)
    val destUri = DocumentsContract.createDocument(
        context.contentResolver,
        destDir,
        mimeType,
        fileName.ifBlank { "output" } + ".${mimeType.substringAfter("/")}"
    )
    try {
        destUri?.let {
            withContext(Dispatchers.IO) {
                context.contentResolver.openOutputStream(destUri)?.use { output ->
                    val fileOld = File(filePath)
//                    val fileUri: Uri? = FileProvider.getUriForFile(
//                        context,
//                        "com.media.mixer.fileprovider",
//                        fileOld)
//                    fileUri?.let {
//                      context.contentResolver.openInputStream(fileUri)?.use {input->
//                          input.copyTo(output)
//                      }
//                    }
                    fileOld.inputStream().use { input ->
                        input.copyTo(output)
                    }
                }
            }
            Toast.makeText(context, "File created successfully", Toast.LENGTH_SHORT).show()
            Log.i("TAG", "saveAs oldPath: ${filePath},mimes: $mimeType")
            Log.i("TAG", "saveAs path: ${destUri.path}")
        }
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
        Toast.makeText(context, "File not found", Toast.LENGTH_SHORT).show()
    } catch (e: IOException) {
        e.printStackTrace()
        Toast.makeText(context, "File not found", Toast.LENGTH_SHORT).show()
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
        Toast.makeText(context, "File not found", Toast.LENGTH_SHORT).show()
    }
}

fun shareContent(path: String, context: Context) {
    Log.d("TAG", "shareContent: $path")
    if (path.contains("storage/emulated") && !path.contains("file://")) {
        val fileOld = File(path)
        val fileUri: Uri? = try {
            FileProvider.getUriForFile(
                context,
                "com.media.mixer.fileprovider",
                fileOld
            )
        } catch (e: IllegalArgumentException) {
            Log.e(
                "File Selector",
                "The selected file can't be shared: $fileOld"
            )
            null
        }
        fileUri?.let {
            val intent = Intent(Intent.ACTION_SEND)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.type = context.contentResolver.getType(it) ?: "*/*"
            intent.putExtra(Intent.EXTRA_STREAM, it)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            ContextCompat.startActivity(context, Intent.createChooser(intent, null), null)
        }
    } else {
        val newpath = Uri.parse(path)
        val file = DocumentFile.fromSingleUri(context, newpath)!!
        if (file.exists()) {
            val intent = Intent(Intent.ACTION_SEND)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.type = context.contentResolver.getType(newpath) ?: "*/*"
            intent.putExtra(Intent.EXTRA_STREAM, newpath)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            ContextCompat.startActivity(context, Intent.createChooser(intent, null), null)
        } else {
            Toast.makeText(context, "File can't be shared", Toast.LENGTH_SHORT).show()
        }
    }

}

@OptIn(UnstableApi::class)
fun createComposition(mediaItem: MediaItem, bundle: Bundle?): Composition {
    val editedMediaItemBuilder = EditedMediaItem.Builder(mediaItem)
    if (bundle != null) {
        val audioProcessors = createAudioProcessorsFromBundle(bundle)
        val videoEffects = createVideoEffectsFromBundle(bundle)
        editedMediaItemBuilder
            .setRemoveAudio(bundle.getBoolean(Util.SHOULD_REMOVE_AUDIO))
            .setRemoveVideo(bundle.getBoolean(Util.SHOULD_REMOVE_VIDEO))
            .setFlattenForSlowMotion(
                bundle.getBoolean(Util.SHOULD_FLATTEN_FOR_SLOW_MOTION)
            )
            .setEffects(Effects(audioProcessors, videoEffects))
    }
    val compositionBuilder =
        Composition.Builder(EditedMediaItemSequence(editedMediaItemBuilder.build()))
    if (bundle != null) {
        compositionBuilder
            .setHdrMode(bundle.getInt(Util.HDR_MODE))
            .experimentalSetForceAudioTrack(
                bundle.getBoolean(Util.FORCE_AUDIO_TRACK)
            )
    }
    return compositionBuilder.build()
}

@OptIn(UnstableApi::class)
fun createCompositionForMerger(mediaItems: List<MediaItem>): Composition {
    val editedMediaItemBuilderList = ImmutableList.Builder<EditedMediaItem>()
    editedMediaItemBuilderList.addAll(mediaItems.map { EditedMediaItem.Builder(it).build() })
    val sequence = EditedMediaItemSequence(editedMediaItemBuilderList.build())
    val compositionBuilder =
        Composition.Builder(sequence)
    return compositionBuilder.build()
}

@OptIn(UnstableApi::class)
fun createAudioProcessorsFromBundle(bundle: Bundle): ImmutableList<AudioProcessor> {
    val selectedAudioEffects =
        bundle.getBooleanArray(Util.AUDIO_EFFECTS_SELECTIONS)
            ?: return ImmutableList.of()
    val processors = ImmutableList.Builder<AudioProcessor>()
    if (selectedAudioEffects[Util.HIGH_PITCHED_INDEX]
        || selectedAudioEffects[Util.SAMPLE_RATE_INDEX]
    ) {
        val sonicAudioProcessor = SonicAudioProcessor()
        if (selectedAudioEffects[Util.HIGH_PITCHED_INDEX]) {
            sonicAudioProcessor.setPitch(2f)
        }
        if (selectedAudioEffects[Util.SAMPLE_RATE_INDEX]) {
            sonicAudioProcessor.setOutputSampleRateHz(48000)
        }
        processors.add(sonicAudioProcessor)
    }
    if (selectedAudioEffects[Util.SKIP_SILENCE_INDEX]) {
        val silenceSkippingAudioProcessor = SilenceSkippingAudioProcessor()
        silenceSkippingAudioProcessor.setEnabled(true)
        processors.add(silenceSkippingAudioProcessor)
    }
    val mixToMono = selectedAudioEffects[Util.CHANNEL_MIXING_INDEX]
    val scaleVolumeToDouble = selectedAudioEffects[Util.VOLUME_SCALING_INDEX]
    if (mixToMono || scaleVolumeToDouble) {
        val mixingAudioProcessor = ChannelMixingAudioProcessor()
        for (inputChannelCount in 1..6) {
            val matrix: ChannelMixingMatrix = if (mixToMono) {
                val mixingCoefficients = FloatArray(inputChannelCount)
                // Each channel is equally weighted in the mix to mono.
                Arrays.fill(mixingCoefficients, 1f / inputChannelCount)
                ChannelMixingMatrix(
                    inputChannelCount,  /* outputChannelCount= */1, mixingCoefficients
                )
            } else {
                // Identity matrix.
                ChannelMixingMatrix.create(
                    inputChannelCount,  /* outputChannelCount= */inputChannelCount
                )
            }

            // Apply the volume adjustment.
            mixingAudioProcessor.putChannelMixingMatrix(
                if (scaleVolumeToDouble) matrix.scaleBy(2f) else matrix
            )
        }
        processors.add(mixingAudioProcessor)
    }
    return processors.build()
}

@OptIn(UnstableApi::class)
fun createVideoEffectsFromBundle(bundle: Bundle): ImmutableList<Effect> {
    val selectedEffects = bundle.getBooleanArray(Util.VIDEO_EFFECTS_SELECTIONS)
        ?: return ImmutableList.of()
    val effects = ImmutableList.Builder<Effect>()
    if (selectedEffects[Util.COLOR_FILTERS_INDEX]) {
        when (bundle.getInt(Util.COLOR_FILTER_SELECTION)) {
            Util.COLOR_FILTER_GRAYSCALE -> effects.add(RgbFilter.createGrayscaleFilter())
            Util.COLOR_FILTER_INVERTED -> effects.add(RgbFilter.createInvertedFilter())
            Util.COLOR_FILTER_SEPIA -> {
                // W3C Sepia RGBA matrix with sRGB as a target color space:
                // https://www.w3.org/TR/filter-effects-1/#sepiaEquivalent
                // The matrix is defined for the sRGB color space and the Transformer library
                // uses a linear RGB color space internally. Meaning this is only for demonstration
                // purposes and it does not display a correct sepia frame.
                val sepiaMatrix = floatArrayOf(
                    0.393f,
                    0.349f,
                    0.272f,
                    0f,
                    0.769f,
                    0.686f,
                    0.534f,
                    0f,
                    0.189f,
                    0.168f,
                    0.131f,
                    0f,
                    0f,
                    0f,
                    0f,
                    1f
                )
                effects.add(RgbMatrix { presentationTimeUs: Long, useHdr: Boolean -> sepiaMatrix })
            }

            else -> throw IllegalStateException(
                "Unexpected color filter "
                        + bundle.getInt(Util.COLOR_FILTER_SELECTION)
            )
        }
    }
    if (selectedEffects[Util.MAP_WHITE_TO_GREEN_LUT_INDEX]) {
        val length = 3
        val mapWhiteToGreenLut = Array(length) {
            Array(length) {
                IntArray(length)
            }
        }
        val scale = 255 / (length - 1)
        for (r in 0 until length) {
            for (g in 0 until length) {
                for (b in 0 until length) {
                    mapWhiteToGreenLut[r][g][b] = Color.rgb( /* red= */r * scale,  /* green= */
                        g * scale,  /* blue= */
                        b * scale
                    )
                }
            }
        }
        mapWhiteToGreenLut[length - 1][length - 1][length - 1] = Color.GREEN
        effects.add(SingleColorLut.createFromCube(mapWhiteToGreenLut))
    }
    if (selectedEffects[Util.RGB_ADJUSTMENTS_INDEX]) {
        effects.add(
            RgbAdjustment.Builder()
                .setRedScale(bundle.getFloat(Util.RGB_ADJUSTMENT_RED_SCALE))
                .setGreenScale(bundle.getFloat(Util.RGB_ADJUSTMENT_GREEN_SCALE))
                .setBlueScale(bundle.getFloat(Util.RGB_ADJUSTMENT_BLUE_SCALE))
                .build()
        )
    }
    if (selectedEffects[Util.HSL_ADJUSTMENT_INDEX]) {
        effects.add(
            HslAdjustment.Builder()
                .adjustHue(bundle.getFloat(Util.HSL_ADJUSTMENTS_HUE))
                .adjustSaturation(bundle.getFloat(Util.HSL_ADJUSTMENTS_SATURATION))
                .adjustLightness(bundle.getFloat(Util.HSL_ADJUSTMENTS_LIGHTNESS))
                .build()
        )
    }
    if (selectedEffects[Util.CONTRAST_INDEX]) {
        effects.add(Contrast(bundle.getFloat(Util.CONTRAST_VALUE)))
    }
    val scaleX = bundle.getFloat(Util.SCALE_X,  /* defaultValue= */1f)
    val scaleY = bundle.getFloat(Util.SCALE_Y,  /* defaultValue= */1f)
    val rotateDegrees =
        bundle.getFloat(Util.ROTATE_DEGREES,  /* defaultValue= */0f)
    if ((scaleX != 1f) || (scaleY != 1f) || (rotateDegrees != 0f)) {
        effects.add(
            ScaleAndRotateTransformation.Builder()
                .setScale(scaleX, scaleY)
                .setRotationDegrees(rotateDegrees)
                .build()
        )
    }
    val resolutionHeight = bundle.getInt(
        Util.RESOLUTION_HEIGHT,  /* defaultValue= */
        C.LENGTH_UNSET
    )
    if (resolutionHeight != C.LENGTH_UNSET) {
        effects.add(Presentation.createForHeight(resolutionHeight))
    }
    return effects.build()
}


@OptIn(UnstableApi::class)
fun startExport(
    uri: String,
    bundle: Bundle,
    context: Context,
    outputFile: File,
    onStart: (Transformer) -> Unit,
    onCompletion: (String) -> Unit,
    onException: (String) -> Unit
) {
    try {
        val outputFilePath: String = outputFile.path
        val newUri =
            if (uri.contains("storage/emulated") && !uri.contains("file://")) Uri.fromFile(File(uri)) else Uri.parse(
                uri
            )
        Log.d("TAG", "startExport: $outputFilePath,newuri:$newUri")
        val mediaItem = createMediaItem(bundle, newUri.toString())
        val transformer =
            createTransformer(
                bundle,
                context,
                onCompletion = { onCompletion(outputFilePath) },
                onException = onException
            )
        val composition = createComposition(mediaItem, bundle)

        transformer.start(composition, outputFilePath)
        onStart(transformer)
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

@OptIn(UnstableApi::class)
fun startExportForMerger(
    trimItemLists: List<TrimItem>,
    bundle: Bundle,
    context: Context,
    outputFile: File,
    onStart: (Transformer) -> Unit,
    onCompletion: (String) -> Unit,
    onException: (String) -> Unit
) {
    try {
        val outputFilePath: String = outputFile.path
        Log.d("TAG", "startExport: $outputFilePath")
        val transformer =
            createTransformer(
                bundle,
                context,
                onCompletion = { onCompletion(outputFilePath) },
                onException = onException
            )
        val composition =
            createCompositionForMerger(trimItemLists.map { createMediaItemForTrim(it) })

        transformer.start(composition, outputFilePath)
        onStart(transformer)
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

fun createBundle(
    removeAudio: Boolean,
    removeVideo: Boolean,
    selectedVideoMimeType: String? = null,
    selectedAudioMimeType: String? = null,
    trimCheckBox: Boolean = false,
    start: Long = 0,
    end: Long = 0
): Bundle {
    val bundle = Bundle()
    bundle.putBoolean(Util.SHOULD_REMOVE_AUDIO, removeAudio)
    bundle.putBoolean(Util.SHOULD_REMOVE_VIDEO, removeVideo)


    selectedAudioMimeType?.let {
        bundle.putString(
            Util.AUDIO_MIME_TYPE,
            selectedAudioMimeType
        )
    }
    selectedVideoMimeType?.let {
        bundle.putString(
            Util.VIDEO_MIME_TYPE,
            selectedVideoMimeType
        )
    }

    if (trimCheckBox) {
        bundle.putLong(
            Util.TRIM_START_MS,
            start
        )
        bundle.putLong(
            Util.TRIM_END_MS,
            end
        )
    }
    bundle.putBoolean(Util.ENABLE_FALLBACK, true)
    return bundle
}

val storagePermission = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> Manifest.permission.READ_MEDIA_VIDEO
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> Manifest.permission.READ_EXTERNAL_STORAGE
    else -> Manifest.permission.WRITE_EXTERNAL_STORAGE
}
val Uri.isSchemaContent: Boolean
    get() = ContentResolver.SCHEME_CONTENT.equals(scheme, ignoreCase = true)

object Utils {

    /**
     * Formats the given duration in milliseconds to a string in the format of `mm:ss` or `hh:mm:ss`.
     */
    fun formatDurationMillis(millis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(hours)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) -
                TimeUnit.MINUTES.toSeconds(minutes) -
                TimeUnit.HOURS.toSeconds(hours)
        return if (hours > 0) {
            String.format(Locale.ENGLISH,"%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.ENGLISH,"%02d:%02d", minutes, seconds)
        }
    }


    fun formatFileSize(size: Long): String {
        val kb = 1024
        val mb = kb * 1024
        val gb = mb * 1024

        return when {
            size < kb -> "$size B"
            size < mb -> "%.2f KB".format(size / kb.toDouble())
            size < gb -> "%.2f MB".format(size / mb.toDouble())
            else -> "%.2f GB".format(size / gb.toDouble())
        }
    }

}