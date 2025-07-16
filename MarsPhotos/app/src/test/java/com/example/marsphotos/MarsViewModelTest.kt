package com.example.marsphotos

import com.example.marsphotos.fake.FakeDataSource
import com.example.marsphotos.fake.FakeNetworkMarsPhotosRepository
import com.example.marsphotos.rules.TestDispatcherRule
import com.example.marsphotos.ui.screens.MarsUiState
import com.example.marsphotos.ui.screens.MarsViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class MarsViewModelTest {
    /**
     * 创建一个测试调度程序
     */
    @get:Rule
    val testDispatcher = TestDispatcherRule()

    /**
     * MarsViewModel 使用 viewModelScope.launch() 调用仓库。此指令会在默认协程调度
     * 程序（称为 Main 调度程序）下启动一个新的协程。由于 Main 调度程序仅适用于界面上下文，
     * 因此您必须将其替换为支持单元测试的调度程序。
     *
     * Kotlin 协程库为此提供了一个名为 TestDispatcher 的协程调度程序。在任何创建新协程的
     * 单元测试中，都需要使用 TestDispatcher（而非 Main 调度程序）
     */
    @Test
    fun marsViewModel_getMarsPhotos_verifyMarsUiStateSuccess() =
        runTest {
            val marsViewModel = MarsViewModel(
                marsPhotosRepository = FakeNetworkMarsPhotosRepository()
            )
            assertEquals(
                MarsUiState.Success(FakeDataSource.photosList),
                marsViewModel.marsUiState
            )
        }
}