package com.techdroidcentre.music.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.core.content.ContextCompat

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
        Column {
            Text(text = "The app need read permission in order to fetch songs from device")
            Button(
                onClick = {
                    permissionLauncher.launch(permission)
                }
            ) {
                Text(text = "Grant")
            }
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