/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.bluromatic.data

import android.content.Context
import android.net.Uri
import androidx.lifecycle.asFlow
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.bluromatic.IMAGE_MANIPULATION_WORK_NAME
import com.example.bluromatic.KEY_BLUR_LEVEL
import com.example.bluromatic.KEY_IMAGE_URI
import com.example.bluromatic.TAG_OUTPUT
import com.example.bluromatic.getImageUri
import com.example.bluromatic.workers.BlurWorker
import com.example.bluromatic.workers.CleanupWorker
import com.example.bluromatic.workers.SaveImageToFileWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapNotNull

class WorkManagerBluromaticRepository(context: Context) : BluromaticRepository {

    /**
     * 通过上下文获取全局唯一的 WorkManager 实例
     */
    private val workManager = WorkManager.getInstance(context)

    /**
     *  利用 workRequest 的标签 TAG_OUTPUT 获取 WorkInfo
     */
    override val outputWorkInfo: Flow<WorkInfo> =
        workManager.getWorkInfosByTagLiveData(TAG_OUTPUT).asFlow().mapNotNull {
            if (it.isNotEmpty()) it.first() else null
        }

    /**
     * 通过调用上下文方法来获取图片的 Uri
     */
    private var imageUri: Uri = context.getImageUri()


    /**
     * Create the WorkRequests to apply the blur and save the resulting image
     *
     * @param blurLevel The amount to blur the image
     */
    override fun applyBlur(blurLevel: Int) {
        // Add WorkRequest to Cleanup temporary images
        // var continuation = workManager.beginWith(OneTimeWorkRequest.from(CleanupWorker::class.java))
        var continuation = workManager
            .beginUniqueWork(
                IMAGE_MANIPULATION_WORK_NAME,   // 唯一标识
                ExistingWorkPolicy.REPLACE,     // 设置任务执行策略
                OneTimeWorkRequest.from(CleanupWorker::class.java)
            )

        // Create low battery constraint
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        // Add WorkRequest to blur the image
        val blur = OneTimeWorkRequestBuilder<BlurWorker>()
            .setInputData(createInputDataForWorkRequest(blurLevel, imageUri))   // 为 WorkRequest 设置输入数据
            .setConstraints(constraints)    // 设置约束条件
            .build()
        continuation = continuation.then(blur)

        // Start the work
        // val blurBuilder = OneTimeWorkRequestBuilder<BlurWorker>()
        // blurBuilder.setInputData(createInputDataForWorkRequest(blurLevel, imageUri))
        // workManager.enqueue(blurBuilder.build())

        // Add WorkRequest to save the image to the filesystem
        val save = OneTimeWorkRequestBuilder<SaveImageToFileWorker>()
            .addTag(TAG_OUTPUT) // 标记工作请求
            .build()
        continuation = continuation.then(save)

        // Start the work
        continuation.enqueue()
    }

    /**
     * Cancel any ongoing WorkRequests
     *
     * 传入唯一链名称 IMAGE_MANIPULATION_WORK_NAME，以便调用仅取消具有该名称的已调度工作
     * */
    override fun cancelWork() {
        workManager.cancelUniqueWork(IMAGE_MANIPULATION_WORK_NAME)
    }

    /**
     * Creates the input data bundle which includes the blur level to
     * update the amount of blur to be applied and the Uri to operate on
     *
     * @return Data which contains the Image Uri as a String and blur level as an Integer
     */
    private fun createInputDataForWorkRequest(blurLevel: Int, imageUri: Uri): Data {
        val builder = Data.Builder()
        builder.putString(KEY_IMAGE_URI, imageUri.toString()).putInt(KEY_BLUR_LEVEL, blurLevel)
        return builder.build()
    }
}
