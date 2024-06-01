package com.moco.androidspecifics.practice

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore.Audio.Media
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.TextToolbar
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moco.androidspecifics.practice.ui.theme.AndroidSpecificsPracticeTheme

class NotificationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        playMedia()
        setContent {
            AndroidSpecificsPracticeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NotificationScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    private var mediaPlayer: MediaPlayer? = null

    /*
        Play the notification sound when the activity is created.
     */
    private fun playMedia() {
        mediaPlayer = MediaPlayer.create(this, /* TODO: Include the ringtone here (res/raw/ folder) */).apply {
            isLooping = true
        }
        mediaPlayer?.start()
    }

    /*
        Remove the media player when the activity is destroyed, to free up resources.
     */
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.stop()
        mediaPlayer = null
    }
}

@Preview
@Composable
fun NotificationScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.clicked_on_notification),
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center
            )
            Spacer(
                modifier = Modifier.height(32.dp)
            )
            Image(
                painter = painterResource(id = /* TODO: Include the notification bell vector here. Distinguish between day and night mode. Use SVG files in res/raw/ directory and create a vector drawable for each. */) ,
                contentDescription = null
            )
        }
    }
}
