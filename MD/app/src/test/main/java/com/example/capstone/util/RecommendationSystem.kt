package com.example.capstone.util

import android.content.Context
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.Locale
import kotlin.math.max
import kotlin.math.min


class RecommendationSystem(context: Context, modelPath: String) {
    private val tflite: Interpreter

    init {
        tflite = Interpreter(loadModelFile(context, modelPath))
    }

    @Throws(IOException::class)
    private fun loadModelFile(context: Context, modelPath: String): MappedByteBuffer {
        val assetFileDescriptor = context.assets.openFd(modelPath)
        val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = fileInputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun predict(userId: Int, movieId: Int): Float {
        val movieInput = arrayOf(floatArrayOf(movieId.toFloat()))
        val userInput = arrayOf(floatArrayOf(userId.toFloat()))
        val output = Array(1) { FloatArray(1) }

        tflite.runForMultipleInputsOutputs(arrayOf(movieInput, userInput), mapOf(0 to output))


        var predictedRating = denormalizeRating(output[0][0])

        predictedRating = max(1.0f, min(predictedRating, 5.0f))

        val formattedRating = String.format(Locale.US, "%.1f", predictedRating).toFloat()

        return formattedRating
    }

    private fun denormalizeRating(rating: Float, minRating: Float = 0f, maxRating: Float = 5f): Float {
        return rating * (maxRating - minRating) + minRating
    }

}
