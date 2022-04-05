package com.ticonsys.exoplayercompose

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.hls.HlsDataSourceFactory
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util
import com.ticonsys.exoplayercompose.ui.theme.ExoplayerComposeTheme

private const val MEDIA_URL =
    "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4"

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExoplayerComposeTheme {
                ExoPlayerView()
            }
        }
    }
}

@Composable
fun ExoPlayerView() {
    val context = LocalContext.current
    val uri = Uri.parse(MEDIA_URL)
    val mediaItem = MediaItem.fromUri(uri)
    val mediaSource = HlsMediaSource.Factory(getHlsDataSourceFactory(context, ""))
        .createMediaSource(mediaItem)
    val exoPlayer = remember(context) {
        ExoPlayer.Builder(context).build().apply {
            addMediaSource(mediaSource)
            prepare()
            playWhenReady = true
            play()
        }
    }

    val playerView = StyledPlayerView(context).apply {
        this.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
        this.setShowNextButton(false)
        this.setShowPreviousButton(false)
        player = exoPlayer
    }
    AndroidView(
        factory = {
            playerView
        }
    )
}

private fun getHlsDataSourceFactory(context: Context, token: String): HlsDataSourceFactory {

    val factory = HlsDataSourceFactory {
        val dataSource: HttpDataSource = buildDataSource(context)
        dataSource.setRequestProperty("cookie", token)
        dataSource
    }
    return factory
}

private fun getDataSourceFactory(context: Context): DataSource.Factory {
    return DefaultHttpDataSource.Factory().setUserAgent(
        Util.getUserAgent(
            context,
            "ExoplayerCompose"
        )
    )
}

private fun buildDataSource(context: Context): HttpDataSource {
    return DefaultHttpDataSource.Factory()
        .setUserAgent(Util.getUserAgent(context, "ExoplayerCompose"))
        .setConnectTimeoutMs(DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS)
        .setReadTimeoutMs(120000)
        .setAllowCrossProtocolRedirects(true).createDataSource()

}
