# Changelog

All notable changes to this project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.1.0] - 2026-05-14

### Added
- Initial release of `OtpField` for Compose Multiplatform.
- Three visual styles: `Boxed`, `Underlined`, `Rounded`.
- Auto-advance focus on input and auto-back on delete.
- **Multiplatform clipboard paste** — a single paste fills every box on Android, iOS, Desktop
  and Web. Pastes longer than `length` are truncated; pastes containing invalid characters are
  rejected outright (no partial fill).
- Keyboard modes via `KeyboardType` — numeric types restrict input to digits, others accept any
  non-whitespace character.
- Obscure (PIN) mode with `obscureChar`, including a configurable brief reveal of the most
  recently typed character (`obscureCharShownDelay`).
- Error state with a horizontal shake animation and an optional `errorMessage`.
- Hardware-keyboard support on Desktop and Web (typing and backspace).
- Blinking cursor in the active box; disabled state; right-to-left layout support.
- Uncontrolled and state-hoisted (`value` / `onValueChange`) overloads.
- Targets: Android, iOS (x64, arm64, simulatorArm64), Desktop (JVM), Web (wasmJs).

[Unreleased]: https://github.com/NadeemIqbal/otp-field/compare/v0.1.0...HEAD
[0.1.0]: https://github.com/NadeemIqbal/otp-field/releases/tag/v0.1.0
