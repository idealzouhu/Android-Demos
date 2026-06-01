/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ai.onnxruntime.example.imageclassifier

import android.graphics.*
import androidx.camera.core.ImageProxy
import java.io.ByteArrayOutputStream
import java.nio.FloatBuffer

const val DIM_BATCH_SIZE = 1;
const val DIM_PIXEL_SIZE = 3;
const val IMAGE_SIZE_X = 224;
const val IMAGE_SIZE_Y = 224;

/**
 * 对Bitmap图像进行预处理，将其转换为ONNX模型所需的浮点型输入数据。
 *
 * 该函数执行以下操作：
 * 1. 将Bitmap的像素数据提取到整型数组中
 * 2. 遍历每个像素，分离RGB三个通道
 * 3. 将像素值从[0, 255]范围归一化到[0, 1]
 * 4. 使用ImageNet数据集的均值和标准差进行标准化处理
 *    - R通道: (value/255 - 0.485) / 0.229
 *    - G通道: (value/255 - 0.456) / 0.224
 *    - B通道: (value/255 - 0.406) / 0.225
 * 5. 将数据按通道分离存储（planar格式）：先所有R，再所有G，最后所有B
 *
 * @param bitmap 输入的Bitmap图像，尺寸应为224x224像素
 * @return 包含预处理后数据的FloatBuffer，尺寸为[1, 3, 224, 224]（batch, channel, height, width）
 */
fun preProcess(bitmap: Bitmap): FloatBuffer {
    val imgData = FloatBuffer.allocate(
            DIM_BATCH_SIZE
                    * DIM_PIXEL_SIZE
                    * IMAGE_SIZE_X
                    * IMAGE_SIZE_Y
    )
    imgData.rewind()
    val stride = IMAGE_SIZE_X * IMAGE_SIZE_Y
    val bmpData = IntArray(stride)
    bitmap.getPixels(bmpData, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
    for (i in 0..IMAGE_SIZE_X - 1) {
        for (j in 0..IMAGE_SIZE_Y - 1) {
            val idx = IMAGE_SIZE_Y * i + j
            val pixelValue = bmpData[idx]
            imgData.put(idx, (((pixelValue shr 16 and 0xFF) / 255f - 0.485f) / 0.229f))
            imgData.put(idx + stride, (((pixelValue shr 8 and 0xFF) / 255f - 0.456f) / 0.224f))
            imgData.put(idx + stride * 2, (((pixelValue and 0xFF) / 255f - 0.406f) / 0.225f))
        }
    }

    imgData.rewind()
    return imgData
}
