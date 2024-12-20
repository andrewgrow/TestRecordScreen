package com.example.test.testrecordscreenapp

import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.test.testrecordscreenapp.screen.MainScreen
import com.example.test.testrecordscreenapp.screen.ScreenRecordService
import com.example.test.testrecordscreenapp.ui.theme.TestRecordScreenAppTheme
import timber.log.Timber

class MainActivity : ComponentActivity() {
    private val mediaProjectionManager: MediaProjectionManager by lazy {
        getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    }

    private val outputPath: String by lazy { "${externalCacheDir?.absolutePath}/screen_record.mp4" }

    private var isRecordingRequested = false

    companion object {
        private const val REQUEST_CODE_MEDIA_PROJECTION = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFullScreenMode()

        setContent {
            TestRecordScreenAppTheme {
                MainScreen(
                    onBackButtonClickListener = ::onBackButtonClicked,
                    onRecordButtonClicked = ::handleRecordButtonClick
                )
            }
        }
    }

    private fun handleRecordButtonClick() {
        if (ScreenRecordService.isRecording()) {
            Timber.e("Stop recording via button click")
            ScreenRecordService.stopService(this)
        } else {
            Timber.w("Recording path: $outputPath")
            isRecordingRequested = true
            ScreenRecordService.startService(
                this,
                resources.displayMetrics,
                outputPath
            )
            startMediaProjectionRequest()
        }
    }

    override fun onDestroy() {
        ScreenRecordService.stopService(this)
        super.onDestroy()
    }

    private fun setFullScreenMode() {
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    fun onBackButtonClicked() {
        if (ScreenRecordService.isRecording()) {
            ScreenRecordService.stopService(this)
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun startMediaProjectionRequest() {
        val intent = mediaProjectionManager.createScreenCaptureIntent()
        startActivityForResult(intent, REQUEST_CODE_MEDIA_PROJECTION)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_MEDIA_PROJECTION) {
            if (resultCode == RESULT_OK && isRecordingRequested) {
                val mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data!!)
                ScreenRecordService.provideMediaProjection(mediaProjection)
            } else {
                ScreenRecordService.stopService(this)
                isRecordingRequested = false
            }
        }
    }
}

