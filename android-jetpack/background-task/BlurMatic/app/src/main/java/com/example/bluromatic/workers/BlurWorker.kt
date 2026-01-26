package com.example.bluromatic.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.bluromatic.DELAY_TIME_MILLIS
import com.example.bluromatic.KEY_BLUR_LEVEL
import com.example.bluromatic.KEY_IMAGE_URI
import com.example.bluromatic.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 日志标签
 */
private const val TAG = "BlurWorker"

/**
 * 模糊处理图片的 Worker
 */
class BlurWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    val resourceUri = inputData.getString(KEY_IMAGE_URI)
    val blurLevel = inputData.getInt(KEY_BLUR_LEVEL, 1)

    override suspend fun doWork(): Result {
        // 告知用户模糊处理 worker 已启动并对图片进行模糊处理
        makeStatusNotification(
            applicationContext.resources.getString(R.string.blurring_image),
            applicationContext
        )

        // 执行实际图片模糊处理工作
        // 在对 withContext() 的调用内传递 Dispatchers.IO，以便 lambda 函数针对潜在阻塞型 IO 操作在特殊线程池中运行
        return withContext(Dispatchers.IO) {
            // 由于此 worker 的运行速度非常快，添加延迟以模拟运行速度较慢的工作
            delay(DELAY_TIME_MILLIS)

            return@withContext try {
                // 确保传入的图片 Uri 不为空
                require(!resourceUri.isNullOrBlank()) {
                    val errorMessage =
                        applicationContext.resources.getString(R.string.invalid_input_uri)
                    Log.e(TAG, errorMessage)
                    errorMessage
                }

                // 从传入的图片 Uri 中获取图片
                val resolver = applicationContext.contentResolver
                val picture = BitmapFactory.decodeStream(
                    resolver.openInputStream(Uri.parse(resourceUri))
                )
                // val picture = BitmapFactory.decodeResource(
                //     applicationContext.resources,
                //     R.drawable.android_cupcake
                // )

                // 模糊图片
                val output = blurBitmap(picture, blurLevel)
                // val output = blurBitmap(picture, 1)

                // 将模糊图片保存到文件
                val outputUri = writeBitmapToFile(applicationContext, output)

                // 告知用户模糊处理 worker 已完成并返回模糊处理图片的 Uri
                val outputData = workDataOf(KEY_IMAGE_URI to outputUri.toString())
                // makeStatusNotification(
                //     "Output is $outputUri",
                //     applicationContext
                // )
                Result.success(outputData)
            } catch (throwable: Throwable) {
                Log.e(
                    TAG,
                    applicationContext.resources.getString(R.string.error_applying_blur),
                    throwable
                )
                Result.failure()
            }
        }

    }

}