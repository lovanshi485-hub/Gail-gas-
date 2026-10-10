package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FileMediaHelper {

    fun createMediaFile(context: Context, isVideo: Boolean = false): Pair<File, Uri> {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val suffix = if (isVideo) ".mp4" else ".jpg"
        val prefix = if (isVideo) "VID_" else "IMG_"
        val storageDir = context.getExternalFilesDir(if (isVideo) "Videos" else "Pictures")
            ?: context.filesDir
        val file = File(storageDir, "${prefix}${timeStamp}${suffix}")
        val uri = androidx.core.content.FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        return Pair(file, uri)
    }

    fun copyUriToAppStorage(context: Context, sourceUri: Uri): String? {
        return try {
            val contentResolver = context.contentResolver
            val inputStream: InputStream? = contentResolver.openInputStream(sourceUri)
            val fileName = "ATT_${System.currentTimeMillis()}"
            val targetFile = File(context.filesDir, fileName)
            val outputStream = FileOutputStream(targetFile)
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            targetFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun makePhoneCall(context: Context, phoneNumber: String) {
        val cleanNumber = phoneNumber.replace(" ", "").replace("-", "")
        if (cleanNumber.isBlank()) {
            Toast.makeText(context, "No phone number available", Toast.LENGTH_SHORT).show()
            return
        }
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$cleanNumber")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Cannot open dialer: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
