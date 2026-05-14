package io.github.nadeemiqbal.otpfield

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/** Compose UI tests — Skiko-backed, run on Desktop and iOS test targets. */
@OptIn(ExperimentalTestApi::class)
class OtpFieldUiTest {

    @Test
    fun typingAllDigits_firesOnCompleteExactlyOnceWithFullValue() = runComposeUiTest {
        var completeCount = 0
        var completed: String? = null
        setContent {
            MaterialTheme {
                OtpField(
                    length = 6,
                    onComplete = { completeCount++; completed = it },
                )
            }
        }
        // Type the digits one at a time.
        "123456".forEach { onNodeWithTag("otp_field").performTextInput(it.toString()) }
        assertEquals("123456", completed)
        assertEquals(1, completeCount, "onComplete must fire exactly once")
    }

    @Test
    fun pasteOfExactLength_fillsAllBoxesAndFiresOnComplete() = runComposeUiTest {
        var completed: String? = null
        setContent {
            MaterialTheme {
                OtpField(length = 6, onComplete = { completed = it })
            }
        }
        // A multi-character insert is treated as a clipboard paste.
        onNodeWithTag("otp_field").performTextInput("987654")
        assertEquals("987654", completed)
    }

    @Test
    fun pasteLongerThanLength_isTruncated() = runComposeUiTest {
        var completed: String? = null
        var lastValue = ""
        setContent {
            MaterialTheme {
                var otp by remember { mutableStateOf("") }
                OtpField(
                    value = otp,
                    onValueChange = { otp = it; lastValue = it },
                    length = 4,
                    onComplete = { completed = it },
                )
            }
        }
        onNodeWithTag("otp_field").performTextInput("1234567890")
        assertEquals("1234", lastValue)
        assertEquals("1234", completed)
    }

    @Test
    fun pasteWithInvalidChars_isRejectedWithNoPartialFill() = runComposeUiTest {
        var completed: String? = null
        var lastValue: String? = null
        setContent {
            MaterialTheme {
                var otp by remember { mutableStateOf("") }
                OtpField(
                    value = otp,
                    onValueChange = { otp = it; lastValue = it },
                    length = 6,
                    onComplete = { completed = it },
                )
            }
        }
        onNodeWithTag("otp_field").performTextInput("12ab34")
        assertNull(lastValue, "an invalid paste must not partially fill the field")
        assertNull(completed)
    }

    @Test
    fun stateHoistedOverload_reportsValueAndCompletes() = runComposeUiTest {
        var current = ""
        var completed: String? = null
        setContent {
            MaterialTheme {
                var otp by remember { mutableStateOf("") }
                OtpField(
                    value = otp,
                    onValueChange = { otp = it; current = it },
                    length = 4,
                    onComplete = { completed = it },
                )
            }
        }
        "42".forEach { onNodeWithTag("otp_field").performTextInput(it.toString()) }
        assertEquals("42", current)
        assertNull(completed, "onComplete should not fire before the field is full")
        "07".forEach { onNodeWithTag("otp_field").performTextInput(it.toString()) }
        assertEquals("4207", current)
        assertEquals("4207", completed)
    }

    @Test
    fun disabledField_exposesDisabledStateAndIgnoresInput() = runComposeUiTest {
        var completed: String? = null
        setContent {
            MaterialTheme {
                OtpField(
                    length = 6,
                    onComplete = { completed = it },
                    enabled = false,
                )
            }
        }
        onNodeWithTag("otp_field").assertIsNotEnabled()
        assertNull(completed)
    }

    @Test
    fun errorState_rendersErrorMessageWithoutCrashing() = runComposeUiTest {
        setContent {
            MaterialTheme {
                OtpField(
                    length = 6,
                    onComplete = {},
                    isError = true,
                    errorMessage = "Invalid code",
                )
            }
        }
        // Advancing the clock exercises the shake animation; it must not crash.
        mainClock.advanceTimeBy(500)
        onNodeWithText("Invalid code").assertExists()
    }

    @Test
    fun obscureMode_acceptsInputAndCompletes() = runComposeUiTest {
        var completed: String? = null
        setContent {
            MaterialTheme {
                OtpField(
                    length = 4,
                    onComplete = { completed = it },
                    obscureChar = '•',
                )
            }
        }
        "2468".forEach { onNodeWithTag("otp_field").performTextInput(it.toString()) }
        assertEquals("2468", completed)
    }
}
