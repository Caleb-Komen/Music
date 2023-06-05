package com.techdroidcentre.common

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Size
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.Q)
fun getThumbnail(context: Context, artworkUri: String): Bitmap? {
    return try {
        context.contentResolver.loadThumbnail(
            Uri.parse(artworkUri),
            Size(300, 300),
            null
        )
    } catch(e: Exception) {
        null
    }
}