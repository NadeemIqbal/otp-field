package io.github.nadeemiqbal.otpfield

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.time.Duration

/**
 * A polished segmented OTP / PIN input.
 *
 * This overload keeps the entered value internally — provide a [length] and an [onComplete]
 * callback and you're done. Use the state-hoisted overload if you need to read or reset the value.
 *
 * ```
 * OtpField(
 *     length = 6,
 *     onComplete = { otp -> viewModel.verify(otp) },
 * )
 * ```
 *
 * @param length the number of digit boxes. Must be greater than 0.
 * @param onComplete called exactly once with the full value when the last box is filled.
 * @param modifier the [Modifier] applied to the field.
 * @param style the [OtpFieldStyle] preset.
 * @param shape the [androidx.compose.ui.graphics.Shape] of each box (ignored by [OtpFieldStyle.Underlined]).
 * @param boxSize the size of a single digit box.
 * @param boxSpacing the gap between digit boxes.
 * @param colors the [OtpFieldColors] used to render the field.
 * @param keyboardType the keyboard to show. Numeric types (`Number`, `NumberPassword`, `Decimal`,
 *   `Phone`) restrict input to digits; other types accept any non-whitespace character.
 * @param obscureChar when non-null, entered characters are masked with this character (PIN mode).
 * @param obscureCharShownDelay when masking, how long the most recently typed character stays
 *   visible before being obscured. [Duration.ZERO] obscures immediately.
 * @param isError when `true`, every box is drawn in the error color and a horizontal shake plays.
 * @param errorMessage optional text rendered below the field while [isError] is `true`.
 * @param enabled when `false`, the field ignores all input and is drawn muted.
 * @throws IllegalArgumentException if [length] is not greater than 0.
 */
@Composable
fun OtpField(
    length: Int,
    onComplete: (String) -> Unit,
    modifier: Modifier = Modifier,
    style: OtpFieldStyle = OtpFieldDefaults.Style,
    shape: androidx.compose.ui.graphics.Shape = OtpFieldDefaults.shapeFor(style),
    boxSize: DpSize = OtpFieldDefaults.BoxSize,
    boxSpacing: Dp = OtpFieldDefaults.BoxSpacing,
    colors: OtpFieldColors = OtpFieldDefaults.colors(),
    keyboardType: KeyboardType = KeyboardType.NumberPassword,
    obscureChar: Char? = null,
    obscureCharShownDelay: Duration = Duration.ZERO,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true,
) {
    var internalValue by remember { mutableStateOf("") }
    OtpField(
        value = internalValue,
        onValueChange = { internalValue = it },
        length = length,
        onComplete = onComplete,
        modifier = modifier,
        style = style,
        shape = shape,
        boxSize = boxSize,
        boxSpacing = boxSpacing,
        colors = colors,
        keyboardType = keyboardType,
        obscureChar = obscureChar,
        obscureCharShownDelay = obscureCharShownDelay,
        isError = isError,
        errorMessage = errorMessage,
        enabled = enabled,
    )
}

/**
 * A polished segmented OTP / PIN input — state-hoisted overload.
 *
 * The entered value is owned by the caller via [value] / [onValueChange], so it can be read,
 * reset, or pre-filled. Auto-advance, backspace, multiplatform clipboard paste, obscure mode and
 * the error shake all work the same as the simpler overload.
 *
 * ```
 * var otp by remember { mutableStateOf("") }
 * OtpField(
 *     value = otp,
 *     onValueChange = { otp = it },
 *     length = 6,
 *     onComplete = { viewModel.verify(it) },
 * )
 * ```
 *
 * @param value the current value. Characters that don't pass the [keyboardType] filter, and any
 *   characters beyond [length], are ignored when rendering.
 * @param onValueChange called with the new, sanitized value whenever it changes.
 * @param length the number of digit boxes. Must be greater than 0.
 * @param onComplete called exactly once when the value reaches [length] characters.
 * @param modifier the [Modifier] applied to the field.
 * @param style the [OtpFieldStyle] preset.
 * @param shape the [androidx.compose.ui.graphics.Shape] of each box (ignored by [OtpFieldStyle.Underlined]).
 * @param boxSize the size of a single digit box.
 * @param boxSpacing the gap between digit boxes.
 * @param colors the [OtpFieldColors] used to render the field.
 * @param keyboardType the keyboard to show; numeric types restrict input to digits.
 * @param obscureChar when non-null, entered characters are masked with this character.
 * @param obscureCharShownDelay how long the most recently typed character stays visible before
 *   being masked. [Duration.ZERO] obscures immediately.
 * @param isError when `true`, every box is drawn in the error color and a horizontal shake plays.
 * @param errorMessage optional text rendered below the field while [isError] is `true`.
 * @param enabled when `false`, the field ignores all input and is drawn muted.
 * @throws IllegalArgumentException if [length] is not greater than 0.
 */
@Composable
fun OtpField(
    value: String,
    onValueChange: (String) -> Unit,
    length: Int,
    onComplete: (String) -> Unit,
    modifier: Modifier = Modifier,
    style: OtpFieldStyle = OtpFieldDefaults.Style,
    shape: androidx.compose.ui.graphics.Shape = OtpFieldDefaults.shapeFor(style),
    boxSize: DpSize = OtpFieldDefaults.BoxSize,
    boxSpacing: Dp = OtpFieldDefaults.BoxSpacing,
    colors: OtpFieldColors = OtpFieldDefaults.colors(),
    keyboardType: KeyboardType = KeyboardType.NumberPassword,
    obscureChar: Char? = null,
    obscureCharShownDelay: Duration = Duration.ZERO,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true,
) {
    requireValidOtpLength(length)

    val isCharValid = remember(keyboardType) { charValidatorFor(keyboardType) }
    val sanitized = remember(value, length, isCharValid) { value.filter(isCharValid).take(length) }
    val styleSpec = remember(style, shape) { resolveOtpStyle(style, shape) }

    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }

    // Index of the character to briefly reveal in obscure mode (-1 = reveal nothing).
    var revealedIndex by remember { mutableStateOf(-1) }
    LaunchedEffect(revealedIndex, obscureCharShownDelay) {
        if (revealedIndex >= 0 && obscureCharShownDelay > Duration.ZERO) {
            delay(obscureCharShownDelay)
            revealedIndex = -1
        }
    }

    // Horizontal-shake animation, replayed each time the field enters the error state.
    val shakeOffset = remember { Animatable(0f) }
    LaunchedEffect(isError) {
        if (isError) {
            shakeOffset.snapTo(0f)
            for (target in shakeOffsets()) {
                shakeOffset.animateTo(target, tween(durationMillis = 45))
            }
        } else {
            shakeOffset.snapTo(0f)
        }
    }

    val handleChange: (String) -> Unit = handleChange@{ raw ->
        if (!enabled) return@handleChange
        val processed = processOtpInput(sanitized, raw, length, isCharValid) ?: return@handleChange
        if (processed != sanitized) {
            val wasFull = sanitized.length == length
            revealedIndex = if (processed.length == sanitized.length + 1) processed.length - 1 else -1
            onValueChange(processed)
            if (processed.length == length && !wasFull) {
                onComplete(processed)
            }
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(modifier = Modifier.offset { IntOffset(shakeOffset.value.roundToInt(), 0) }) {
            OtpBoxesRow(
                value = sanitized,
                length = length,
                boxSize = boxSize,
                boxSpacing = boxSpacing,
                styleSpec = styleSpec,
                colors = colors,
                isFocused = isFocused,
                isError = isError,
                enabled = enabled,
                obscureChar = obscureChar,
                revealedIndex = revealedIndex,
            )
            BasicTextField(
                value = TextFieldValue(text = sanitized, selection = TextRange(sanitized.length)),
                onValueChange = { handleChange(it.text) },
                modifier = Modifier
                    .matchParentSize()
                    .testTag("otp_field")
                    .focusRequester(focusRequester)
                    .onFocusChanged { isFocused = it.isFocused },
                enabled = enabled,
                readOnly = false,
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                // The real glyphs and cursor are transparent — the visible digits are drawn by
                // OtpBoxesRow underneath. The text field only captures input, paste and focus.
                textStyle = TextStyle(color = Color.Transparent),
                cursorBrush = SolidColor(Color.Transparent),
            )
        }
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = colors.errorTextColor,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 6.dp),
            )
        }
    }
}

@Composable
private fun OtpBoxesRow(
    value: String,
    length: Int,
    boxSize: DpSize,
    boxSpacing: Dp,
    styleSpec: OtpStyleSpec,
    colors: OtpFieldColors,
    isFocused: Boolean,
    isError: Boolean,
    enabled: Boolean,
    obscureChar: Char?,
    revealedIndex: Int,
) {
    val activeIndex = value.length.coerceAtMost(length - 1)
    Row(horizontalArrangement = Arrangement.spacedBy(boxSpacing)) {
        for (index in 0 until length) {
            val char = value.getOrNull(index)
            val isActive = enabled && isFocused && index == activeIndex
            OtpBox(
                char = char,
                isActive = isActive,
                isError = isError,
                enabled = enabled,
                boxSize = boxSize,
                styleSpec = styleSpec,
                colors = colors,
                obscured = obscureChar != null && index != revealedIndex,
                obscureChar = obscureChar,
                showCursor = isActive && char == null,
            )
        }
    }
}

@Composable
private fun OtpBox(
    char: Char?,
    isActive: Boolean,
    isError: Boolean,
    enabled: Boolean,
    boxSize: DpSize,
    styleSpec: OtpStyleSpec,
    colors: OtpFieldColors,
    obscured: Boolean,
    obscureChar: Char?,
    showCursor: Boolean,
) {
    val targetBorderColor = when {
        !enabled -> colors.disabledBorderColor
        isError -> colors.errorBorderColor
        isActive -> colors.focusedBorderColor
        else -> colors.borderColor
    }
    val borderColor by animateColorAsState(targetBorderColor)
    val textColor = if (enabled) colors.textColor else colors.disabledTextColor
    val strokeWidth = if (isActive || isError) 2.dp else 1.dp

    val base = Modifier.size(width = boxSize.width, height = boxSize.height)
    val decorated = when {
        styleSpec.drawBox -> base
            .clip(styleSpec.shape)
            .background(
                if (enabled) colors.boxBackgroundColor else colors.boxBackgroundColor.copy(alpha = 0.5f),
            )
            .border(BorderStroke(strokeWidth, borderColor), styleSpec.shape)
        styleSpec.drawUnderline -> base.drawBehind {
            val sw = strokeWidth.toPx()
            val y = size.height - sw / 2f
            drawLine(borderColor, Offset(0f, y), Offset(size.width, y), sw)
        }
        else -> base
    }

    Box(modifier = decorated, contentAlignment = Alignment.Center) {
        val display: Char? = when {
            char == null -> null
            obscured && obscureChar != null -> obscureChar
            else -> char
        }
        if (display != null) {
            Text(
                text = display.toString(),
                color = textColor,
                style = MaterialTheme.typography.titleLarge,
            )
        }
        if (showCursor) {
            BlinkingCursor(color = colors.cursorColor)
        }
    }
}

@Composable
private fun BlinkingCursor(color: Color) {
    val transition = rememberInfiniteTransition()
    val alpha by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(durationMillis = 600), RepeatMode.Reverse),
    )
    Box(
        Modifier
            .size(width = 2.dp, height = 22.dp)
            .alpha(alpha)
            .background(color),
    )
}
