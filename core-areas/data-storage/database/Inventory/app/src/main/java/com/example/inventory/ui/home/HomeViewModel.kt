/*
 * Copyright (C) 2023 The Android Open Source Project
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

package com.example.inventory.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventory.data.Item
import com.example.inventory.data.ItemsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel to retrieve all items in the Room database.
 */
class HomeViewModel(itemsRepository: ItemsRepository) : ViewModel() {
    // 定义一个名为homeUiState的StateFlow属性，用于管理Home界面的状态
    val homeUiState: StateFlow<HomeUiState> =
        itemsRepository.getAllItemsStream()
            // 将获取到的数据流映射为HomeUiState对象，以适应UI展示需求
            .map { HomeUiState(it) }
            // 使用stateIn扩展函数将映射后的流转换为StateFlow
            .stateIn(
                // 指定作用域为viewModelScope，即与ViewModel的生命周期一致
                scope = viewModelScope,
                // 配置流的共享策略为WhileSubscribed，当没有订阅者时暂停流的活动，以优化资源使用
                // 并指定超时时间为TIMEOUT_MILLIS，超过这个时间没有订阅者将自动停止流的活动
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                // 设置初始值为一个空的HomeUiState对象，确保在数据流开始之前UI有默认状态展示
                initialValue = HomeUiState()
            )


    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

/**
 * Ui State for HomeScreen
 */
data class HomeUiState(val itemList: List<Item> = listOf())
