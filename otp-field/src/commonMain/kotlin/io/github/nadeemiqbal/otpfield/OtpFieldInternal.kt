package io.github.nadeemiqbal.otpfield

import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.input.KeyboardType

/**
 * Throws [IllegalArgumentException] when [length] is not a valid OTP length.
 *
 * Extracted from the composable so it can be unit-tested directly without composition.
 */
internal fun requireValidOtpLength(length: Int) {
    require(length > 0) {
        "OtpField length must be greater than 0; was $length."
    }
}

/**
 * Returns the per-character validator for a [keyboardType]. Numeric keyboards accept digits only;
 * every other keyboard accepts any non-whitespace, non-control character.
 */
internal fun charValidatorFor(keyboardType: KeyboardType): (Char) -> Boolean = when (keyboardType) {
    KeyboardType.Number,
    KeyboardType.NumberPassword,
    KeyboardType.Decimal,
    KeyboardType.Phone,
    -> { ch -> ch.isDigit() }

    else -> { ch -> !ch.isWhitespace() && !ch.isISOControl() }
}

/**
 * Pure input-processing logic for [OtpField]. Returns the new field value, or `null` when the
 * input should be rejected entirely (the field keeps [old]).
 *
 * - Single-character edits (typing one char, or deleting) drop invalid characters and truncate
 *   the result to [length].
 * - Multi-character additions are treated as a clipboard paste: if *any* character is invalid the
 *   entire paste is rejected (no partial fill); otherwise the paste is truncated to [length].
 */
internal fun processOtpInput(
    old: String,
    new: String,
    length: Int,
    isCharValid: (Char) -> Boolean,
): String? {
    if (new == old) return null
    val added = new.length - old.length
    return if (added > 1) {
        // Treated as a paste — all-or-nothing.
        if (new.any { !isCharValid(it) }) null else new.take(length)
    } else {
        // Typing a single character, or deleting.
        val filtered = new.filter(isCharValid).take(length)
        if (filtered == old) null else filtered
    }
}

/**
 * The horizontal-offset keyframes, in pixels, of the error shake animation. The sequence starts
 * and ends at `0f` so the field settles back exactly in place.
 */
internal fun shakeOffsets(): List<Float> = listOf(0f, -12f, 10f, -8f, 6f, -3f, 0f)

/** Resolved, non-color visual attributes for an [OtpFieldStyle]. */
internal data class OtpStyleSpec(
    val drawBox: Boolean,
    val drawUnderline: Boolean,
    val shape: Shape,
)

/** Maps an [OtpFieldStyle] (and the caller-supplied box [shape]) to an [OtpStyleSpec]. */
internal fun resolveOtpStyle(style: OtpFieldStyle, shape: Shape): OtpStyleSpec = when (style) {
    OtpFieldStyle.Boxed -> OtpStyleSpec(drawBox = true, drawUnderline = false, shape = shape)
    OtpFieldStyle.Rounded -> OtpStyleSpec(drawBox = true, drawUnderline = false, shape = shape)
    OtpFieldStyle.Underlined -> OtpStyleSpec(drawBox = false, drawUnderline = true, shape = RectangleShape)
}
