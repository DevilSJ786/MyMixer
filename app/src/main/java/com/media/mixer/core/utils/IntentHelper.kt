package com.media.mixer.core.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.documentfile.provider.DocumentFile

object IntentHelper {
    fun openFile(context: Context, uri: Uri) {
        val target = Intent().apply {
            action = Intent.ACTION_VIEW
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            setDataAndType(uri, context.contentResolver.getType(uri))
        }
        val chooser = Intent.createChooser(target, "Share")
        startActivity(context, chooser)
    }

    fun shareFile(context: Context, file: DocumentFile) {
        val uri = file.uri
        val target = Intent().apply {
            action = Intent.ACTION_SEND
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            type = context.contentResolver.getType(uri)
            putExtra(Intent.EXTRA_STREAM, uri)
        }
        val chooser = Intent.createChooser(target, "Share")
        startActivity(context, chooser)
    }


    private fun startActivity(context: Context, intent: Intent) {
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, e.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }
}
