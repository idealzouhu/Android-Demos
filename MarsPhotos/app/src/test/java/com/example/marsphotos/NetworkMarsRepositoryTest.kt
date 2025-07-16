package com.example.marsphotos

import com.example.marsphotos.data.NetworkMarsPhotosRepository
import com.example.marsphotos.fake.FakeDataSource
import com.example.marsphotos.fake.FakeMarsApiService
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.assertEquals

class NetworkMarsRepositoryTest {

    /**
     * 在使用虚构 API 服务的前提下，测试仓库的 getMarsPhotos() 方法
     *
     * 协程测试库提供 runTest() 函数。该函数接受您在 lambda 中传递的方法，并通过 TestScope（从
     * CoroutineScope 继承而来）运行该方法
     */
    @Test
    fun networkMarsPhotosRepository_getMarsPhotos_verifyPhotoList() =
        runTest {
            val repository = NetworkMarsPhotosRepository(
                marsApiService = FakeMarsApiService()
            )
            assertEquals(FakeDataSource.photosList, repository.getMarsPhotos())
        }
}