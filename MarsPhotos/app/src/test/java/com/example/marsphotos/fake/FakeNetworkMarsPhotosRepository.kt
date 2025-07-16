package com.example.marsphotos.fake

import com.example.marsphotos.model.MarsPhoto
import com.example.marsphotos.data.MarsPhotosRepository

/**
 * 创建一个从 MarsPhotosRepository 接口继承的虚构类，并替换 getMarsPhotos() 函数以返回虚构数据
 */
class FakeNetworkMarsPhotosRepository : MarsPhotosRepository{
    override suspend fun getMarsPhotos(): List<MarsPhoto> {
        return FakeDataSource.photosList
    }
}