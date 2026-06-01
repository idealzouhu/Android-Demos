package com.example.modular.feature.detail;

import com.example.modular.common.service.ILastOpenedNoteProvider;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

/**
 * Hilt 模块：将组件间通信接口绑定到本模块的实现，供其他组件注入使用。
 */
@Module
@InstallIn(SingletonComponent.class)
public abstract class DetailHiltModule {

    @Binds
    public abstract ILastOpenedNoteProvider bindLastOpenedNoteProvider(LastOpenedNoteProviderImpl impl);
}
