package io.github.nadeemiqbal.otpfield

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

/** Default values and factory functions used by [OtpField]. */
object OtpFieldDefaults {

    /** The default visual style. */
    val Style: OtpFieldStyle = OtpFieldStyle.Boxed

    /** The default size of a single digit box. */
    val BoxSize: DpSize = DpSize(48.dp, 56.dp)

    /** The default gap between digit boxes. */
    val BoxSpacing: Dp = 8.dp

    /** Returns the box [Shape] that best matches [style]. */
    fun shapeFor(style: OtpFieldStyle): Shape = when (style) {
        OtpFieldStyle.Boxed -> RoundedCornerShape(8.dp)
        OtpFieldStyle.Rounded -> RoundedCornerShape(16.dp)
        OtpFieldStyle.Underlined -> RectangleShape
    }

    /**
     * Creates an [OtpFieldColors] derived from the current [MaterialTheme]. Any parameter can be
     * overridden; anything left at its default is resolved from the active color scheme.
     */
    @Composable
    @ReadOnlyComposable
    fun colors(
        textColor: Color = MaterialTheme.colorScheme.onSurface,
        boxBackgroundColor: Color = MaterialTheme.colorScheme.surface,
        borderColor: Color = MaterialTheme.colorScheme.outline,
        focusedBorderColor: Color = MaterialTheme.colorScheme.primary,
        errorBorderColor: Color = MaterialTheme.colorScheme.error,
        cursorColor: Color = MaterialTheme.colorScheme.primary,
        disabledBorderColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.38f),
        disabledTextColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        errorTextColor: Color = MaterialTheme.colorScheme.error,
    ): OtpFieldColors = OtpFieldColors(
        textColor = textColor,
        boxBackgroundColor = boxBackgroundColor,
        borderColor = borderColor,
        focusedBorderColor = focusedBorderColor,
        errorBorderColor = errorBorderColor,
        cursorColor = cursorColor,
        disabledBorderColor = disabledBorderColor,
        disabledTextColor = disabledTextColor,
        errorTextColor = errorTextColor,
    )
}
