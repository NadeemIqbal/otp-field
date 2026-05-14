package io.github.nadeemiqbal.otpfield

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * The color set used to render an [OtpField].
 *
 * Obtain an instance from [OtpFieldDefaults.colors], optionally overriding individual colors,
 * or construct one directly for full control.
 *
 * @property textColor color of the entered digits.
 * @property boxBackgroundColor fill color of each box ([OtpFieldStyle.Boxed]/[OtpFieldStyle.Rounded]).
 * @property borderColor border/underline color of an inactive box.
 * @property focusedBorderColor border/underline color of the currently active box.
 * @property errorBorderColor border/underline color of every box while `isError` is `true`.
 * @property cursorColor color of the blinking cursor in the active box.
 * @property disabledBorderColor border/underline color when the field is disabled.
 * @property disabledTextColor digit color when the field is disabled.
 * @property errorTextColor color of the `errorMessage` text.
 */
@Immutable
class OtpFieldColors(
    val textColor: Color,
    val boxBackgroundColor: Color,
    val borderColor: Color,
    val focusedBorderColor: Color,
    val errorBorderColor: Color,
    val cursorColor: Color,
    val disabledBorderColor: Color,
    val disabledTextColor: Color,
    val errorTextColor: Color,
) {
    /** Returns a copy of this color set, overriding only the values that are passed. */
    fun copy(
        textColor: Color = this.textColor,
        boxBackgroundColor: Color = this.boxBackgroundColor,
        borderColor: Color = this.borderColor,
        focusedBorderColor: Color = this.focusedBorderColor,
        errorBorderColor: Color = this.errorBorderColor,
        cursorColor: Color = this.cursorColor,
        disabledBorderColor: Color = this.disabledBorderColor,
        disabledTextColor: Color = this.disabledTextColor,
        errorTextColor: Color = this.errorTextColor,
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OtpFieldColors) return false
        return textColor == other.textColor &&
            boxBackgroundColor == other.boxBackgroundColor &&
            borderColor == other.borderColor &&
            focusedBorderColor == other.focusedBorderColor &&
            errorBorderColor == other.errorBorderColor &&
            cursorColor == other.cursorColor &&
            disabledBorderColor == other.disabledBorderColor &&
            disabledTextColor == other.disabledTextColor &&
            errorTextColor == other.errorTextColor
    }

    override fun hashCode(): Int {
        var result = textColor.hashCode()
        result = 31 * result + boxBackgroundColor.hashCode()
        result = 31 * result + borderColor.hashCode()
        result = 31 * result + focusedBorderColor.hashCode()
        result = 31 * result + errorBorderColor.hashCode()
        result = 31 * result + cursorColor.hashCode()
        result = 31 * result + disabledBorderColor.hashCode()
        result = 31 * result + disabledTextColor.hashCode()
        result = 31 * result + errorTextColor.hashCode()
        return result
    }

    override fun toString(): String =
        "OtpFieldColors(textColor=$textColor, boxBackgroundColor=$boxBackgroundColor, " +
            "borderColor=$borderColor, focusedBorderColor=$focusedBorderColor, " +
            "errorBorderColor=$errorBorderColor, cursorColor=$cursorColor, " +
            "disabledBorderColor=$disabledBorderColor, disabledTextColor=$disabledTextColor, " +
            "errorTextColor=$errorTextColor)"
}
