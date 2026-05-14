package io.github.nadeemiqbal.otpfield

/**
 * Visual style presets for [OtpField].
 *
 * - [Boxed] — each digit sits in a filled, bordered box with slightly rounded corners.
 * - [Underlined] — each digit sits above a single bottom rule; no fill, no full border.
 * - [Rounded] — like [Boxed] but with a larger corner radius for a softer, pill-ish look.
 */
enum class OtpFieldStyle {
    Boxed,
    Underlined,
    Rounded,
}
