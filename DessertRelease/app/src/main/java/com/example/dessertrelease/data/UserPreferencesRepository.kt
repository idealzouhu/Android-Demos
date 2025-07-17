package com.example.dessertrelease.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

/**
 *  用户首选项存储库
 */
class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    /**
     * 使用 [booleanPreferencesKey] 函数可定义一个键并向其传递名称 is_linear_layout
     * 此键用于访问布尔值，以指明是否应显示线性布局。
     */
    private companion object {
        val IS_LINEAR_LAYOUT = booleanPreferencesKey("is_linear_layout")
        const val TAG = "UserPreferencesRepo"
    }

    /**
     * 创建一个 Flow<Boolean>，该 Flow 读取键 IS_LINEAR_LAYOUT 的值。如果不存在则返回
     * 默认值 true。
     *
     * data 属性是 Preferences 对象的 Flow。Preferences 对象包含 DataStore 中的所有
     * 键值对。DataStore 中的数据每次更新时，系统都会向 Flow 发出一个新的 Preferences 对象。
     *
     * 在 catch 块中，如果存在 IOexception，请记录错误并发出 emptyPreferences()。如果抛出
     * 的是其他类型的异常，建议重新抛出该异常。存在错误时，通过发出 emptyPreferences()，映射函
     * 数仍然可以映射到默认值。
     */
    val isLinearLayout: Flow<Boolean> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[IS_LINEAR_LAYOUT] ?: true
        }

    /**
     * 保存布局首选项
     *
     * 使用 [edit] 函数写入键值对到 DataStore
     */
    suspend fun saveLayoutPreference(isLinearLayout: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_LINEAR_LAYOUT] = isLinearLayout
        }
    }
}