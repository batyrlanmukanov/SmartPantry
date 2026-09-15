package kz.kbtu.smartpantry

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class MainFlowUiTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun signIn_opensInventoryScreen() {
        val inventoryTexts = listOf("Add food item", "Добавить продукт")
        if (inventoryTexts.any { text ->
                composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
            }
        ) {
            val visibleText = inventoryTexts.first { text ->
                composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
            }
            composeRule.onNodeWithText(visibleText).assertIsDisplayed()
            return
        }

        composeRule.onNodeWithText("Sign up", substring = true).performClick()
        composeRule.onNodeWithText("Full name", substring = true).performTextInput("UI Tester")
        composeRule.onNodeWithText("Email", substring = true).performTextInput("ui@test.com")
        composeRule.onNodeWithText("Password", substring = true).performTextInput("123456")
        composeRule.onNodeWithText("Confirm password", substring = true).performTextInput("123456")
        composeRule.onNodeWithText("Create account", substring = true).performClick()

        composeRule.waitUntil(timeoutMillis = 7_000) {
            inventoryTexts.any { text ->
                composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
            }
        }

        val visibleInventoryText = inventoryTexts.first { text ->
            composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText(visibleInventoryText).assertIsDisplayed()
    }
}
