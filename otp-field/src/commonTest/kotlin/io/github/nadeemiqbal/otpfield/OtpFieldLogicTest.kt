package io.github.nadeemiqbal.otpfield

import androidx.compose.ui.text.input.KeyboardType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Pure-logic tests — no composition required. Run on every target including Android unit tests. */
class OtpFieldLogicTest {

    private val digitsOnly = charValidatorFor(KeyboardType.Number)

    @Test
    fun requireValidOtpLength_throwsHelpfulMessageOnZeroOrNegative() {
        val zero = assertFailsWith<IllegalArgumentException> { requireValidOtpLength(0) }
        assertTrue(zero.message?.contains("greater than 0") == true)
        assertFailsWith<IllegalArgumentException> { requireValidOtpLength(-3) }
    }

    @Test
    fun requireValidOtpLength_passesForPositive() {
        requireValidOtpLength(1)
        requireValidOtpLength(8)
    }

    @Test
    fun charValidator_numericKeyboardsAcceptDigitsOnly() {
        val number = charValidatorFor(KeyboardType.Number)
        val numberPassword = charValidatorFor(KeyboardType.NumberPassword)
        assertTrue(number('7'))
        assertFalse(number('a'))
        assertFalse(number(' '))
        assertTrue(numberPassword('0'))
        assertFalse(numberPassword('X'))
    }

    @Test
    fun charValidator_asciiAcceptsNonWhitespacePrintable() {
        val ascii = charValidatorFor(KeyboardType.Ascii)
        assertTrue(ascii('a'))
        assertTrue(ascii('Z'))
        assertTrue(ascii('5'))
        assertTrue(ascii('#'))
        assertFalse(ascii(' '))
        assertFalse(ascii('\n'))
    }

    @Test
    fun processOtpInput_typingValidCharAppends() {
        assertEquals("1", processOtpInput(old = "", new = "1", length = 6, isCharValid = digitsOnly))
        assertEquals("12", processOtpInput(old = "1", new = "12", length = 6, isCharValid = digitsOnly))
    }

    @Test
    fun processOtpInput_typingSingleInvalidCharIsRejected() {
        assertNull(processOtpInput(old = "12", new = "12x", length = 6, isCharValid = digitsOnly))
    }

    @Test
    fun processOtpInput_pasteOfExactLengthFills() {
        assertEquals("123456", processOtpInput(old = "", new = "123456", length = 6, isCharValid = digitsOnly))
    }

    @Test
    fun processOtpInput_pasteLongerThanLengthIsTruncated() {
        assertEquals("123456", processOtpInput(old = "", new = "12345678", length = 6, isCharValid = digitsOnly))
    }

    @Test
    fun processOtpInput_pasteWithAnyInvalidCharIsRejectedEntirely() {
        // No partial fill — the whole paste is dropped.
        assertNull(processOtpInput(old = "", new = "12ab34", length = 6, isCharValid = digitsOnly))
    }

    @Test
    fun processOtpInput_deletionShortensValue() {
        assertEquals("123", processOtpInput(old = "1234", new = "123", length = 6, isCharValid = digitsOnly))
        assertEquals("", processOtpInput(old = "1", new = "", length = 6, isCharValid = digitsOnly))
    }

    @Test
    fun processOtpInput_noChangeReturnsNull() {
        assertNull(processOtpInput(old = "123", new = "123", length = 6, isCharValid = digitsOnly))
    }

    @Test
    fun processOtpInput_typingWhenFullIsRejected() {
        assertNull(processOtpInput(old = "123456", new = "1234567", length = 6, isCharValid = digitsOnly))
    }

    @Test
    fun shakeOffsets_startsAndEndsAtZeroAndOscillates() {
        val offsets = shakeOffsets()
        assertTrue(offsets.isNotEmpty())
        assertEquals(0f, offsets.first())
        assertEquals(0f, offsets.last())
        assertTrue(offsets.any { it < 0f } && offsets.any { it > 0f }, "shake should swing both ways")
    }

    @Test
    fun otpFieldColors_copyOverridesOnlyGivenValues() {
        val base = OtpFieldColors(
            textColor = androidx.compose.ui.graphics.Color.Black,
            boxBackgroundColor = androidx.compose.ui.graphics.Color.White,
            borderColor = androidx.compose.ui.graphics.Color.Gray,
            focusedBorderColor = androidx.compose.ui.graphics.Color.Blue,
            errorBorderColor = androidx.compose.ui.graphics.Color.Red,
            cursorColor = androidx.compose.ui.graphics.Color.Blue,
            disabledBorderColor = androidx.compose.ui.graphics.Color.LightGray,
            disabledTextColor = androidx.compose.ui.graphics.Color.LightGray,
            errorTextColor = androidx.compose.ui.graphics.Color.Red,
        )
        val updated = base.copy(focusedBorderColor = androidx.compose.ui.graphics.Color.Green)
        assertEquals(androidx.compose.ui.graphics.Color.Green, updated.focusedBorderColor)
        assertEquals(base.textColor, updated.textColor)
        assertEquals(base, base.copy())
    }
}
