package com.techdroidcentre.music.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.techdroidcentre.music.R

@Composable
fun PermissionDialog(
    context: Context,
    permission: String,
    permissionAction: (PermissionAction) -> Unit
) {
    if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
        permissionAction(PermissionAction.PermissionGranted)
        return
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            permissionAction(PermissionAction.PermissionGranted)
        } else {
            permissionAction(PermissionAction.PermissionDenied)
        }
    }
    val activity = context as Activity
    val shouldShowPermissionRationale = activity.shouldShowRequestPermissionRationale(permission)

    if (shouldShowPermissionRationale) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Image(
                painter = painterResource(R.drawable.baseline_folder_24),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(192.dp),
                colorFilter = ColorFilter.tint(color = Color(0xFFFCCC52))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "The app need to be granted read permission in order to fetch songs from device",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Thin,
                fontStyle = FontStyle.Italic
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    permissionLauncher.launch(permission)
                }
            ) {
                Text(text = "Grant")
            }
            Spacer(modifier = Modifier.weight(4f))
        }
    } else {
        SideEffect {
            permissionLauncher.launch(permission)
        }
    }
}

sealed interface PermissionAction {
    object PermissionGranted: PermissionAction

    object PermissionDenied: PermissionAction
}