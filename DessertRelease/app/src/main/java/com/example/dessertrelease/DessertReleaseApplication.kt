package com.example.dessertrelease

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.dessertrelease.data.UserPreferencesRepository

/**
 * 实例化的 Preferences Datastore 的名称
 */
private const val LAYOUT_PREFERENCE_NAME = "layout_preferences"

/**
 * 创建一个 DataStore 实例，用于保存用户布局选择
 *
 * 这段代码为 Context 类型添加了一个私有扩展属性 dataStore，其类型为
 * DataStore<Preferences>。 它使用 preferencesDataStore 委托创
 * 建了一个用于持久化存储简单数据的 DataStore 实例。
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = LAYOUT_PREFERENCE_NAME
)

/**
 * 创建一个 Application 类
 *
 * 将 DessertReleaseApplication 类定义为应用的入口点。此代码用于在启动
 * MainActivity 之前，初始化 DessertReleaseApplication 类中定义的依赖项。
 */
class DessertReleaseApplication: Application() {
    lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate() {
        super.onCreate()
        userPreferencesRepository = UserPreferencesRepository(dataStore)
    }
}