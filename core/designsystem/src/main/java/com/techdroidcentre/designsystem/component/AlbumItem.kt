package com.techdroidcentre.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.techdroidcentre.designsystem.icon.MusicIcons
import com.techdroidcentre.model.Album

@Composable
fun AlbumItem(
    album: Album,
    navigateToAlbumDetails: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .clip(shape = MaterialTheme.shapes.small)
            .clickable { navigateToAlbumDetails(album.id.toString()) }
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(album.artworkUri)
                .crossfade(true)
                .build(),
            error = painterResource(MusicIcons.musicRecord),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(shape = MaterialTheme.shapes.small),
            contentScale = ContentScale.Crop
        )
        Text(
            text = album.name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

