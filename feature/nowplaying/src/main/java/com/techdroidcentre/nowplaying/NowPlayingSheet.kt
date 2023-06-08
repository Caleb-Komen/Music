package com.techdroidcentre.nowplaying

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.techdroidcentre.common.getThumbnail

@ExperimentalMaterial3Api
@Composable
fun NowPlayingSheet(
    scaffoldState: BottomSheetScaffoldState,
    modifier: Modifier = Modifier,
    onSheetCollapsed: (Boolean) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val viewModel: NowPlayingViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    var alphaValue by remember { mutableStateOf(1f) }

    BoxWithConstraints(modifier = modifier) {
        val closedSheetHeight = 64.dp
        val collapsedSheetHeight = with(LocalDensity.current) { closedSheetHeight.toPx() }
        val dragRange = constraints.maxHeight - collapsedSheetHeight
        BottomSheetScaffold(
            modifier = Modifier.fillMaxSize(),
            scaffoldState = scaffoldState,
            sheetPeekHeight = closedSheetHeight,
            sheetDragHandle = null,
            sheetShape = BottomSheetDefaults.HiddenShape,
            sheetContent = {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier
                            .height(closedSheetHeight)
                            .fillMaxWidth()
                            .graphicsLayer {
                                alpha = alphaValue
                            }
                            .padding(horizontal = 24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                                        getThumbnail(LocalContext.current, uiState.song.artworkUri)
                                    else uiState.song.artworkData
                                )
                                .crossfade(true)
                                .build(),
                            error = painterResource(androidx.media3.session.R.drawable.media3_icon_circular_play),
                            contentDescription = null,
                            modifier = Modifier
                                .sizeIn(maxWidth = 56.dp, maxHeight = 56.dp)
                                .clip(MaterialTheme.shapes.medium),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = uiState.song.title,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        IconButton(onClick = viewModel::playOrPause) {
                            if (uiState.isPlaying) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_pause_24),
                                    contentDescription = "pause"
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "play"
                                )
                            }
                        }
                        IconButton(onClick = viewModel::playNextSong) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_skip_next_24),
                                contentDescription = "skip to next"
                            )
                        }
                    }
                    Column(
                        modifier = Modifier
                            .graphicsLayer {
                                alpha = 1 - alphaValue
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        BottomSheetDefaults.DragHandle()
                        NowPlayingScreen(
                            uiState = uiState,
                            onPositionChange = { viewModel.seekTo(it.toLong()) },
                            playNextSong = viewModel::playNextSong,
                            playPreviousSong = viewModel::playPreviousSong,
                            playOrPause = viewModel::playOrPause
                        )
                    }
                }
            }
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val fraction = if (scaffoldState.bottomSheetState.requireOffset().isNaN()) {
                    0f
                } else {
                    scaffoldState.bottomSheetState.requireOffset() / dragRange
                }.coerceIn(0f, 1f)
                alphaValue = fraction

                onSheetCollapsed(scaffoldState.bottomSheetState.currentValue == SheetValue.PartiallyExpanded)

                content(it)
            }
        }
    }
}
