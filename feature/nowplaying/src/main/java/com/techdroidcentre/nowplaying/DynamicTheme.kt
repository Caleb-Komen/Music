package com.techdroidcentre.nowplaying

import android.content.Context
import androidx.collection.LruCache
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.size.Scale
import com.techdroidcentre.designsystem.icon.MusicIcons
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.min

// Borrowed from google's compose sample app Jetcaster (https://github.com/android/compose-samples/blob/main/Jetcaster/app/src/main/java/com/example/jetcaster/util/DynamicTheming.kt)
@Composable
fun NowPlayingDynamicTheme(
    artworkData: ByteArray?,
    content: @Composable () -> Unit
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val dominantColorState = rememberDominantColourState(
        defaultColor = MaterialTheme.colorScheme.surface
    ) { color ->
        // We want a color which has sufficient contrast against the surface color
        color.contrastAgainst(surfaceColor) >= 3f
    }
    DynamicThemePrimaryColorsFromImage(dominantColorState) {
        // Update the dominantColorState with colors coming from the song album art
        LaunchedEffect(artworkData) {
            if (artworkData != null) {
                if (artworkData.isNotEmpty()) {
                    dominantColorState.updateColourFromImage(artworkData)
                } else {
                    dominantColorState.reset()
                }
            } else { dominantColorState.updateColourFromImage(byteArrayOf()) }
        }
        content()
    }
}

@Composable
fun DynamicThemePrimaryColorsFromImage(
    dominantColourState: DominantColourState = rememberDominantColourState(),
    content: @Composable () -> Unit
) {
    val colourScheme = MaterialTheme.colorScheme.copy(
        primary = animateColorAsState(
            targetValue = dominantColourState.colour,
            animationSpec = spring(stiffness = Spring.StiffnessLow)
        ).value,
        onPrimary = animateColorAsState(
            targetValue = dominantColourState.onColour,
            animationSpec = spring(stiffness = Spring.StiffnessLow)
        ).value
    )
    MaterialTheme(colorScheme = colourScheme, content = content)
}

@Composable
fun rememberDominantColourState(
    context: Context = LocalContext.current,
    defaultColor: Color = MaterialTheme.colorScheme.primary,
    defaultOnColor: Color = MaterialTheme.colorScheme.onPrimary,
    cacheSize: Int = 12,
    isColourValid: (Color) -> Boolean = { true }
) = remember {
    DominantColourState(context, defaultColor, defaultOnColor, cacheSize, isColourValid)
}

class DominantColourState(
    private val context: Context,
    private val defaultColor: Color,
    private val defaultOnColor: Color,
    cacheSize: Int = 12,
    private val isColourValid: (Color) -> Boolean = { true }
) {
    var colour by mutableStateOf(defaultColor)
        private set

    var onColour by mutableStateOf(defaultOnColor)
        private set

    private val cache = when {
        cacheSize > 0 -> LruCache<String, DominantColour>(cacheSize)
        else -> null
    }

    suspend fun updateColourFromImage(artworkData: ByteArray) {
        val dominantColour = calculateDominantColour(artworkData)
        colour = dominantColour?.colour ?: defaultColor
        onColour = dominantColour?.onColour ?: defaultOnColor
    }

    private suspend fun calculateDominantColour(
        artworkData: ByteArray
    ): DominantColour? {
        val cached = cache?.get(artworkData.toString())
        if (cached != null) return cached

        return calculateSwatchesInImage(context, artworkData)
            .sortedByDescending { swatch ->
                swatch.population
            }.firstOrNull { swatch ->
                isColourValid(Color(swatch.rgb))
            }?.let { swatch ->
                DominantColour(
                    colour = Color(swatch.rgb),
                    onColour = Color(swatch.bodyTextColor)
                )
            }?.also { dominantColour ->
                cache?.put(artworkData.toString(), dominantColour)
            }
    }

    fun reset() {
        colour = defaultColor
        onColour = defaultOnColor
    }
}

const val RESOURCE_URI = "android.resource://com.techdroidcentre.music/drawable/"

private suspend fun calculateSwatchesInImage(
    context: Context,
    artworkData: ByteArray
): List<Palette.Swatch> {
    // song without an album art will use the default music note image.
    val data = if (artworkData.isNotEmpty()) artworkData else RESOURCE_URI + MusicIcons.defaultMusicNote
    val request = ImageRequest.Builder(context)
        .data(data)
        .size(128).scale(Scale.FILL)
        .allowHardware(false)
        .memoryCacheKey("${artworkData.toString()}.palette")
        .build()

    val bitmap = when (val result = context.imageLoader.execute(request)) {
        is SuccessResult -> result.drawable.toBitmap()
        else -> null
    }
    return bitmap?.let {
        withContext(Dispatchers.Default) {
            val palette = Palette.Builder(it)
                .resizeBitmapArea(0)
                .clearFilters()
                .maximumColorCount(8)
                .generate()
            palette.swatches
        }
    } ?: emptyList()
}

data class DominantColour(val colour: Color, val onColour: Color)

fun Color.contrastAgainst(background: Color): Float {
    val fg = if (alpha < 1f) compositeOver(background) else this

    val fgLuminance = fg.luminance() + 0.05f
    val bgLuminance = background.luminance() + 0.05f

    return max(fgLuminance, bgLuminance) / min(fgLuminance, bgLuminance)
}