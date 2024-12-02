package com.example.test.testrecordscreenapp.screen

import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.util.DisplayMetrics
import android.view.Surface
import timber.log.Timber

class ScreenRecorder(
    private val mediaProjection: MediaProjection,
    private val metrics: DisplayMetrics,
    private val outputFilePath: String
) {
    private val mediaRecorder: MediaRecorder = MediaRecorder()
    private var virtualDisplay: VirtualDisplay? = null

    fun startRecording() {
        Timber.w("startRecording()")
        registerMediaProjectionCallback()
        setupMediaRecorder()
        val surface: Surface = mediaRecorder.surface
        virtualDisplay = mediaProjection.createVirtualDisplay(
            "ScreenRecorder",
            metrics.widthPixels,
            metrics.heightPixels,
            metrics.densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
            surface,
            null,
            null
        )
        mediaRecorder.start()
        Timber.w("Recording started successfully")
    }

    private fun registerMediaProjectionCallback() {
        mediaProjection.registerCallback(object : MediaProjection.Callback() {
            override fun onStop() {
                super.onStop()
                Timber.w("MediaProjection stopped")
                stopRecording()
            }
        }, null)
    }

    private fun setupMediaRecorder() {
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE)
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
        mediaRecorder.setVideoEncodingBitRate(8 * 1000 * 1000)
        mediaRecorder.setVideoFrameRate(30)
        mediaRecorder.setVideoSize(metrics.widthPixels, metrics.heightPixels)
        mediaRecorder.setOutputFile(outputFilePath)
        mediaRecorder.prepare()
    }

    fun stopRecording() {
        try {
            mediaRecorder.stop()
            mediaRecorder.reset()
            Timber.d("Recording stopped successfully")
        } catch (e: Exception) {
            Timber.e(e, "Error stopping MediaRecorder")
        } finally {
            virtualDisplay?.release()
            virtualDisplay = null
            Timber.d("VirtualDisplay released")
        }
    }
}