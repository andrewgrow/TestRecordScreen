package com.example.test.testrecordscreenapp.screen

import android.Manifest
import android.content.Context
import android.media.projection.MediaProjection
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    onBackButtonClickListener: (() -> Unit)? = null,
    onRecordButtonClicked: (() -> Unit)? = null,
) {
    val context = LocalContext.current

    var isGranted by remember { mutableStateOf(false) }
    val permission = Manifest.permission.CAMERA
    LaunchedEffect(context) {
        isGranted = ContextCompat.checkSelfPermission(context, permission) == 0
    }

    var isRecording by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { paddingValues ->
        if (isGranted) {
            CameraWithOverlay(
                isButtonActivated = false,
                onRecordButtonClick = { onRecordButtonClicked?.invoke() },
                onBackButtonClick = onBackButtonClickListener
            )
        } else {
            ShowMessage(
                "Camera permission is not granted. Please enable it in settings.",
                paddingValues
            )
        }
    }
}

@Composable
fun CameraWithOverlay(
    isButtonActivated: Boolean,
    onRecordButtonClick: (() -> Unit)? = null,
    onBackButtonClick: (() -> Unit)? = null,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreview(modifier = Modifier.fillMaxSize())
        // Button Record
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(56.dp)
                .size(100.dp)
                .background(if (isButtonActivated) Color.Red else Color.Gray, shape = CircleShape)
                .clickable {
                    onRecordButtonClick?.invoke()
                },
            contentAlignment = Alignment.Center
        ) {
            Text("Record")
        }

        // Button Back
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(36.dp)
                .size(60.dp)
                .background(Color.DarkGray, shape = CircleShape)
                .clickable {
                    onBackButtonClick?.invoke()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Back",
                color = Color.White
            )
        }
    }
}

@Composable
fun ShowMessage(textMsg: String? = null, paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .padding(paddingValues)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = textMsg ?: "Hello, World!")
    }
}