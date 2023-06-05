package com.techdroidcentre.music

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import com.techdroidcentre.music.ui.theme.MusicTheme
import com.techdroidcentre.music.util.PermissionAction
import com.techdroidcentre.music.util.PermissionDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isPermissionGranted by rememberSaveable{ mutableStateOf(false) }

            MusicTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val permission =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                            Manifest.permission.READ_MEDIA_AUDIO
                        else Manifest.permission.READ_EXTERNAL_STORAGE
                    PermissionDialog(
                        context = LocalContext.current,
                        permission = permission,
                        permissionAction = { permissionAction ->
                            isPermissionGranted = when (permissionAction) {
                                PermissionAction.PermissionGranted -> true
                                PermissionAction.PermissionDenied -> false
                            }
                        }
                    )

                    if (isPermissionGranted) {
                        val viewModel: MainViewModel = viewModel()
                        val state by viewModel.uiState.collectAsState()
                        if (state.rootChildren.isNotEmpty()) {
                            MusicApp(rootChildren = state.rootChildren)
                        }
                    }
                }
            }
        }
    }
}
