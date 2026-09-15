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
class AssistantIntegrationTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun authToAssistant_messageFlow_works() {
        val assistantTabTexts = listOf("Assistant", "Ассистент")
        if (!assistantTabTexts.any { text ->
                composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
            }
        ) {
            composeRule.onNodeWithText("Sign up", substring = true).performClick()
            composeRule.onNodeWithText("Full name", substring = true).performTextInput("Integration Tester")
            composeRule.onNodeWithText("Email", substring = true).performTextInput("integration@test.com")
            composeRule.onNodeWithText("Password", substring = true).performTextInput("123456")
            composeRule.onNodeWithText("Confirm password", substring = true).performTextInput("123456")
            composeRule.onNodeWithText("Create account", substring = true).performClick()
        }

        composeRule.waitUntil(timeoutMillis = 7_000) {
            assistantTabTexts.any { text ->
                composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
            }
        }

        val assistantTabText = assistantTabTexts.first { text ->
            composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText(assistantTabText).performClick()
        composeRule.onNodeWithText("Ask the assistant", substring = true).performTextInput("What can I cook?")
        composeRule.onNodeWithText("Send", substring = true).performClick()

        composeRule.waitUntil(timeoutMillis = 7_000) {
            composeRule.onAllNodesWithText("What can I cook?").fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onAllNodesWithText("What can I cook?")[0].assertIsDisplayed()
    }
}
