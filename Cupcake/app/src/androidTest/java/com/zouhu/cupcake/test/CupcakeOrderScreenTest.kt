package com.zouhu.cupcake.test

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.zouhu.cupcake.R
import com.zouhu.cupcake.data.DataSource
import com.zouhu.cupcake.data.OrderUiState
import com.zouhu.cupcake.ui.OrderSummaryScreen
import com.zouhu.cupcake.ui.SelectOptionScreen
import com.zouhu.cupcake.ui.StartOrderScreen
import org.junit.Rule
import org.junit.Test

/**
 * 验证这些屏幕上显示的内容以及在这些屏幕上执行的操作是否会产生正确的结果
 */
class CupcakeOrderScreenTest {
    /**
     * Note: To access to an empty activity, the code uses ComponentActivity instead of
     * MainActivity.
     */
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val fakeOrderUiState = OrderUiState(
        quantity = 6,
        flavor = "Vanilla",
        date = "Wed Jul 21",
        price = "$100",
        pickupOptions = listOf()
    )

    /**
     * When quantity options are provided to StartOrderScreen, the options are displayed on the
     * screen.
     */
    @Test
    fun startOrderScreen_verifyContent() {

        // When StartOrderScreen is loaded
        composeTestRule.setContent {
            StartOrderScreen(
                quantityOptions = DataSource.quantityOptions,
                onNextButtonClicked = {}
            )
        }

        // Then all the options are displayed on the screen.
        DataSource.quantityOptions.forEach {
            composeTestRule.onNodeWithStringId(it.first).assertIsDisplayed()
        }
    }

    /**
     * When list of options and subtotal are provided to SelectOptionScreen,the options and subtotal
     * are displayed on the screen and the next button is disabled.
     */
    @Test
    fun selectOptionScreen_verifyContent() {
        // Given list of options
        val flavors = listOf("Vanilla", "Chocolate", "Hazelnut", "Cookie", "Mango")
        // And subtotal
        val subtotal = "$100"

        // When SelectOptionScreen is loaded
        composeTestRule.setContent {
            SelectOptionScreen(subtotal = subtotal, options = flavors)
        }

        // Then all the options are displayed on the screen.
        flavors.forEach { flavor ->
            composeTestRule.onNodeWithText(flavor).assertIsDisplayed()
        }

        // And then the subtotal is displayed correctly.
        composeTestRule.onNodeWithText(
            composeTestRule.activity.getString(
                R.string.subtotal_price,
                subtotal
            )
        ).assertIsDisplayed()

        // And then the next button is disabled
        composeTestRule.onNodeWithStringId(R.string.next).assertIsNotEnabled()
    }

    /**
     * When list of options and subtotal are provided to SelectOptionScreen, and one of the option
     * is selected, then the next button is enabled.
     */
    @Test
    fun selectOptionScreen_optionSelected_NextButtonEnabled() {
        // Given list of options
        val flavours = listOf("Vanilla", "Chocolate", "Hazelnut", "Cookie", "Mango")
        // And sub total
        val subTotal = "$100"

        // When SelectOptionScreen is loaded
        composeTestRule.setContent {
            SelectOptionScreen(subtotal = subTotal, options = flavours)
        }

        // And one option is selected
        composeTestRule.onNodeWithText("Vanilla").performClick()

        // Then the next button is disabled
        composeTestRule.onNodeWithStringId(R.string.next).assertIsEnabled()
    }

    /**
     * When a OrderUiState is provided to Summary Screen, then the flavor, date and subtotal is
     * displayed on the screen.
     */
    @Test
    fun summaryScreen_verifyContentDisplay() {
        // When Summary Screen is loaded
        composeTestRule.setContent {
            OrderSummaryScreen(
                orderUiState = fakeOrderUiState,
                onCancelButtonClicked = {},
                onSendButtonClicked = { _, _ -> },
            )
        }

        // Then the UI is updated correctly.
        composeTestRule.onNodeWithText(fakeOrderUiState.flavor).assertIsDisplayed()
        composeTestRule.onNodeWithText(fakeOrderUiState.date).assertIsDisplayed()
        composeTestRule.onNodeWithText(
            composeTestRule.activity.getString(
                R.string.subtotal_price,
                fakeOrderUiState.price
            )
        ).assertIsDisplayed()
    }
}