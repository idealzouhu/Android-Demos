package com.example.marsphotos.network

import com.example.marsphotos.model.MarsPhoto
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET

/**
 * Build the retrofit object with a base URL
 */
// private const val BASE_URL =
//     "https://android-kotlin-fun-mars-server.appspot.com"

/**
 * 使用基准网址和转换器工厂创建 Retrofit 对象，以转换字符串
 */
// private val retrofit = Retrofit.Builder()
//     .addConverterFactory(ScalarsConverterFactory.create())
//     .baseUrl(BASE_URL)
//     .build()
// private val retrofit = Retrofit.Builder()
//     .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
//     .baseUrl(BASE_URL)
//     .build()


/**
 * 创建一个可说明 Retrofit 如何与网络服务器通信的接口
 *
 * 调用 getPhotos() 方法时，Retrofit 会将端点 photos 附加到您用于启动请求的基准网址
 */
interface MarsApiService {
    @GET("photos")
    suspend fun getPhotos(): List<MarsPhoto>
}

/**
 * 创建一个单例对象，该对象将保存 MarsApiService 的实例
 */
// object MarsApi {
//     val retrofitService : MarsApiService by lazy {
//         retrofit.create(MarsApiService::class.java)
//     }
// }
