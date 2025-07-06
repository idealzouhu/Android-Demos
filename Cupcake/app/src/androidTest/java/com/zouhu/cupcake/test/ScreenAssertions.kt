package com.zouhu.cupcake.test

import androidx.navigation.NavController
import org.junit.Assert.assertEquals


/**
 * 添加扩展函数
 */
fun NavController.assertCurrentRouteName(expectedRouteName: String) {
    assertEquals(expectedRouteName, currentBackStackEntry?.destination?.route)
}