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
package com.zouhu.racetracker

import com.zouhu.racetracker.ui.RaceParticipant
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.assertEquals

class RaceParticipantTest {
    private val raceParticipant = RaceParticipant(
        name = "Test",
        maxProgress = 100,
        progressDelayMillis = 500L, // 决定了多长时间后比赛进度才会更新
        initialProgress = 0,
        progressIncrement = 1   // 每次进度增加 1
    )

    /**
     * 测试[MarathonParticipant]类中，当比赛开始时，参与者的进度是否能正确更新
     *
     * 注意：在使用 runTest 构建器进行协程测试时，默认情况下它会忽略对 delay() 的实际等待时间，
     * 因此如果你直接在 runTest 中调用 raceParticipant.run()，测试代码会继续执行而不会等待
     * run() 中的 delay() 完成。
     */
    @Test
    fun raceParticipant_RaceStarted_ProgressUpdated() = runTest {
        // 设置期望的进度值，此处为1，表示期望参与者在比赛开始后能前进一个进度单位
        val expectedProgress = 1
        // 启动一个协程来执行参与者的run方法，模拟比赛开始后参与者的行动
        launch { raceParticipant.run() }
        // 假设参与者有一个固定的进度延迟，这里通过advanceTimeBy方法模拟这个延迟
        advanceTimeBy(raceParticipant.progressDelayMillis)
        // 运行当前的协程，确保所有异步操作完成
        runCurrent()
        // 断言参与者的当前进度与期望的进度相等，以验证进度是否正确更新
        assertEquals(expectedProgress, raceParticipant.currentProgress)
    }

    /**
     * 测试[MarathonParticipant]类中，当比赛结束时，参与者的进度是否能正确更新
     */
    @Test
    fun raceParticipant_RaceFinished_ProgressUpdated() = runTest {
        launch { raceParticipant.run() }
        advanceTimeBy(raceParticipant.maxProgress * raceParticipant.progressDelayMillis)
        runCurrent()
        assertEquals(100, raceParticipant.currentProgress)
    }

}
