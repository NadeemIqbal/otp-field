# OtpField

**A polished segmented OTP / PIN input for Compose Multiplatform** — N boxes, auto-advancing
focus, configurable styling, and the feature most OTP components miss: **clipboard paste that
works on every platform**.

[![Maven Central](https://img.shields.io/maven-central/v/io.github.nadeemiqbal/otp-field)](https://central.sonatype.com/artifact/io.github.nadeemiqbal/otp-field)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](LICENSE)
[![Build](https://github.com/NadeemIqbal/otp-field/actions/workflows/build.yml/badge.svg)](https://github.com/NadeemIqbal/otp-field/actions/workflows/build.yml)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.3.21-7F52FF?logo=kotlin)](https://kotlinlang.org)
![Android](https://img.shields.io/badge/Android-24%2B-3DDC84?logo=android&logoColor=white)
![iOS](https://img.shields.io/badge/iOS-x64%20%7C%20arm64%20%7C%20simulator-000000?logo=apple&logoColor=white)
![Desktop](https://img.shields.io/badge/Desktop-JVM-007396?logo=openjdk&logoColor=white)
![Web](https://img.shields.io/badge/Web-wasmJs-654FF0?logo=webassembly&logoColor=white)

<!-- TODO: add hero.gif — record the sample app showing all three styles (Boxed / Underlined /
     Rounded), obscure mode revealing then masking a digit, and a single clipboard paste filling
     every box at once. Drop it at docs/hero.gif and reference it here. -->

## Why this library

Most OTP components are a row of single-character text fields glued together with focus hacks —
and pasting a code from your SMS or password manager either does nothing or only fills the first
box. `OtpField` is built on a **single underlying text field**, so **one paste fills every box on
Android, iOS, Desktop and Web** — and auto-advance, backspace and hardware keyboards all just work.

## Platform support

| Platform | Supported | Tested              |
|----------|:---------:|---------------------|
| Android  |     ✅     | ✅ (unit + UI)       |
| iOS      |     ✅     | ✅ (UI, Skiko)       |
| Desktop  |     ✅     | ✅ (unit + UI)       |
| Web      |     ✅     | ✅ (compile + logic) |

## Installation

`gradle/libs.versions.toml`:

```toml
[libraries]
otp-field = { module = "io.github.nadeemiqbal:otp-field", version = "0.1.0" }
```

`commonMain` dependencies:

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.otp.field)
        }
    }
}
```

## Quick start

```kotlin
OtpField(
    length = 6,
    onComplete = { otp -> viewModel.verify(otp) },
)
```

`onComplete` fires exactly once, with the full code, the moment the last box is filled — whether
the user typed it or pasted it.

## API examples

**State-hoisted** — read, reset or pre-fill the value yourself:

```kotlin
var otp by remember { mutableStateOf("") }
OtpField(
    value = otp,
    onValueChange = { otp = it },
    length = 6,
    onComplete = { viewModel.verify(it) },
)
```

**Pick a style**

```kotlin
OtpField(
    length = 4,
    onComplete = { ... },
    style = OtpFieldStyle.Boxed,        // Boxed | Underlined | Rounded
)
```

**PIN / obscure mode** — mask digits, with a brief reveal of the last one typed:

```kotlin
OtpField(
    length = 4,
    onComplete = { ... },
    obscureChar = '•',
    obscureCharShownDelay = 500.milliseconds,
)
```

**Error state with shake**

```kotlin
OtpField(
    length = 6,
    onComplete = { ... },
    isError = errorState,
    errorMessage = "Invalid code",
)
```

**Sizing & keyboard**

```kotlin
OtpField(
    length = 6,
    onComplete = { ... },
    boxSize = DpSize(48.dp, 56.dp),
    boxSpacing = 8.dp,
    keyboardType = KeyboardType.NumberPassword,   // Number | NumberPassword | Ascii
)
```

## Customization

- **`style`** — `Boxed`, `Underlined` or `Rounded`.
- **`shape`** — overrides the box shape (ignored by `Underlined`).
- **`boxSize` / `boxSpacing`** — size of each box and the gap between them.
- **`colors`** — start from `OtpFieldDefaults.colors()` and `.copy(...)` what you need (border,
  focused border, error border, cursor, text, disabled and error-text colors).
- **`keyboardType`** — numeric types (`Number`, `NumberPassword`, `Decimal`, `Phone`) restrict
  input to digits; other types accept any non-whitespace character. This same filter is what
  rejects invalid pastes.
- **`obscureChar` / `obscureCharShownDelay`** — PIN masking and how long the last digit stays
  visible before it's hidden.
- **`isError` / `errorMessage`** — error styling, shake animation and an optional message.
- **`enabled`** — `false` makes the field inert and muted.

## Multiplatform paste — the killer feature

`OtpField` is backed by one hidden text field rather than N separate ones, so the platform's
own paste action delivers the whole string in a single edit:

- **Exactly `length` characters** → fills every box and fires `onComplete`.
- **Longer than `length`** → truncated to `length`.
- **Contains an invalid character** (e.g. letters with `KeyboardType.Number`) → the whole paste
  is rejected; the field is never left half-filled.

## Per-platform notes

- **iOS** — the system keyboard stays attached to the single underlying field, so it doesn't
  flicker as focus "moves" between boxes the way an N-field implementation would.
- **Android** — works across IMEs; auto-advance is driven by value length, not per-field focus
  hops, which avoids IME-specific focus quirks.
- **Desktop** — full hardware-keyboard support: type to fill, Backspace to go back,
  Cmd/Ctrl+V to paste.
- **Web (wasmJs)** — a single focusable element means tab-focus and browser paste behave
  predictably; no focus-stealing between sub-fields.

## Comparison with alternatives

| | **OtpField** | N separate `TextField`s | A single plain `TextField` |
|---|---|---|---|
| Segmented box visuals | ✅ 3 styles | ⚠️ DIY | ❌ |
| One paste fills all boxes | ✅ every platform | ❌ usually only box 1 | ✅ |
| Auto-advance / auto-back | ✅ | ⚠️ fragile focus hacks | n/a |
| Obscure / PIN mode | ✅ with brief reveal | ⚠️ DIY | ⚠️ DIY |
| Error shake animation | ✅ | ❌ DIY | ❌ DIY |
| Hardware keyboard / backspace | ✅ | ⚠️ flaky across IMEs | ✅ |
| Multiplatform | ✅ Android/iOS/Desktop/Web | ✅ | ✅ |

If you just need a code field and don't care about the segmented look, a plain `TextField` is
genuinely fine. The moment you want the boxed OTP look *and* reliable paste, hand-rolling N
fields is where people get stuck — that's the gap this library fills.

## Roadmap

- Optional auto-submit delay after the last digit
- Per-box enter/exit animations
- Optional haptic feedback on completion (Android/iOS)

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md). Bug reports and feature requests are welcome via GitHub
Issues.

## License

```
Copyright 2026 Nadeem Iqbal

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0
```

See [LICENSE](LICENSE) for the full text.
