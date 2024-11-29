package com.example.test.testrecordscreenapp.screen

import android.Manifest
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { paddingValues ->
        val context = LocalContext.current
        val permission = Manifest.permission.CAMERA
        val isGranted = remember { ContextCompat.checkSelfPermission(context, permission) == 0 }
        if (isGranted) {
            CameraPreview()
        } else {
            val error = "Camera permission is not granted. Please enable it in settings."
            ShowMessage(error, paddingValues)
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