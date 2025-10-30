/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zouhu.cupcake

import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.zouhu.cupcake.data.DataSource
import com.zouhu.cupcake.ui.OrderSummaryScreen
import com.zouhu.cupcake.ui.OrderViewModel
import com.zouhu.cupcake.ui.SelectOptionScreen
import com.zouhu.cupcake.ui.StartOrderScreen

/**
 * 定义 Cupcake 应用的四个路线
 */
enum class CupcakeScreen(@StringRes val title: Int) {
    Start(title = R.string.app_name),     // 启动屏幕, 从三个按钮之一选择纸杯蛋糕的数量。
    Flavor(title = R.string.choose_flavor),    // 选择纸杯蛋糕的口味
    Pickup(title = R.string.choose_pickup_date),    // 选择纸杯蛋糕的取货日期
    Summary(title = R.string.order_summary)    // 显示订单的摘要，然后发送或取消订单
}

/**
 * Composable that displays the topBar and displays back button if back navigation is possible.
 */
@Composable
fun CupcakeAppBar(
    currentScreen: CupcakeScreen,   // 当前屏幕，包含标题
    canNavigateBack: Boolean,       // 仅当返回堆栈上有可组合项时才应显示向上按钮
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}

@Composable
fun CupcakeApp(
    viewModel: OrderViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    // 获取当前屏幕
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = CupcakeScreen.valueOf(
        backStackEntry?.destination?.route ?: CupcakeScreen.Start.name
    )

    Scaffold(
        topBar = {
            CupcakeAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() } // 返回到上一个屏幕
            )
        }
    ) { innerPadding ->
        val uiState by viewModel.uiState.collectAsState()

        NavHost(
            navController = navController,                  // 使用此对象在屏幕之间导航
            startDestination = CupcakeScreen.Start.name,    // 定义应用首次显示 NavHost 时默认显示的目标页面
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = CupcakeScreen.Start.name) {
                StartOrderScreen(
                    quantityOptions = DataSource.quantityOptions,
                    onNextButtonClicked = {
                        viewModel.setQuantity(it)
                        navController.navigate(CupcakeScreen.Flavor.name)  // 导航到 Flavor 页面
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(dimensionResource(R.dimen.padding_medium))
                )

            }
            composable(route = CupcakeScreen.Flavor.name) {
                val context = LocalContext.current
                SelectOptionScreen(
                    subtotal = uiState.price,
                    onNextButtonClicked = { navController.navigate(CupcakeScreen.Pickup.name) },    // 导航到Pickup屏幕
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(viewModel, navController) // 取消订单并导航到Start屏幕
                    },
                    options = DataSource.flavors.map { id -> context.resources.getString(id) },
                    onSelectionChanged = { viewModel.setFlavor(it) },
                    modifier = Modifier.fillMaxHeight()
                )
            }
            composable(route = CupcakeScreen.Pickup.name) {
                SelectOptionScreen(
                    subtotal = uiState.price,
                    onNextButtonClicked = { navController.navigate(CupcakeScreen.Summary.name) },
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(viewModel, navController)
                    },
                    options = uiState.pickupOptions,
                    onSelectionChanged = { viewModel.setDate(it) },
                    modifier = Modifier.fillMaxHeight()
                )
            }
            composable(route = CupcakeScreen.Summary.name) {
                val context = LocalContext.current
                OrderSummaryScreen(
                    orderUiState = uiState,
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(viewModel, navController)
                    },
                    onSendButtonClicked = { subject: String, summary: String -> // 导航到系统分享屏幕界面
                        shareOrder(context, subject = subject, summary = summary)
                    },
                    modifier = Modifier.fillMaxHeight()
                )
            }
        }

    }
}

private fun shareOrder(context: Context, subject: String, summary: String) {
    // 创建一个Intent用于发送文本内容
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, subject) // 邮件主题
        putExtra(Intent.EXTRA_TEXT, summary)    // 邮件内容
    }

    // 使用系统分享对话框展示可选的应用（如邮件、微信等）供用户选择
    context.startActivity(
        Intent.createChooser(
            intent,
            context.getString(R.string.new_cupcake_order)
        )
    )
}

/**
 * 当用户在任何屏幕上点按 Cancel 按钮时，应用会重置视图模型中的状态并调用 popBackStack()
 * 来返回到开始屏幕。
 */
private fun cancelOrderAndNavigateToStart(
    viewModel: OrderViewModel,
    navController: NavHostController
) {
    viewModel.resetOrder()
    navController.popBackStack(CupcakeScreen.Start.name, inclusive = false)
}
