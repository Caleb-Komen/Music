package com.techdroidcentre.music

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.MediaItem
import com.techdroidcentre.music.ui.theme.MusicTheme
import com.techdroidcentre.music.util.PermissionAction
import com.techdroidcentre.music.util.PermissionDialog
import com.techdroidcentre.songs.SongsScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val state by viewModel.uiState.collectAsState()

            MusicTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PermissionDialog(
                        context = LocalContext.current,
                        permission = Manifest.permission.READ_EXTERNAL_STORAGE,
                        permissionAction = { permissionAction ->
                            when (permissionAction) {
                                PermissionAction.PermissionGranted -> viewModel.setPermissionGranted(true)
                                PermissionAction.PermissionDenied -> viewModel.setPermissionGranted(false)
                            }
                        }
                    )

                    if (state.isPermissionGranted && state.rootMediaItem != MediaItem.EMPTY) {
                        MusicApp(rootMediaItem = state.rootMediaItem)
                    } else {
                        Text(text = "The app need read permission in order to fetch songs from device")
                    }
                }
            }
        }
    }
}
