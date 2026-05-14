# Contributing to OtpField

Thanks for your interest in improving this library! Contributions of all kinds are welcome —
bug reports, feature requests, docs, and code.

## Project layout

```
otp-field/                The published Kotlin Multiplatform library.
  src/commonMain/          Public API + implementation (the only place real code lives).
  src/commonTest/          Pure-logic tests — run on every target, including Android unit tests.
  src/skikoTest/           Compose UI tests (runComposeUiTest) — run on Desktop and iOS.
sample/composeApp/         Shared Compose sample UI used by every platform host.
sample/androidApp/         Android launcher.
sample/desktopApp/         Desktop (JVM) launcher.
sample/webApp/             Web (wasmJs) launcher.
sample/iosApp/             iOS launcher (Xcode project).
```

Keep all real library code in `otp-field/src/commonMain` — the library is pure Compose
Multiplatform and should not depend on any platform-specific UI APIs.

## Building & testing

```bash
./gradlew build                          # build + test everything
./gradlew allTests                       # run tests on all targets
./gradlew :otp-field:desktopTest         # fastest feedback: logic + UI tests on Desktop
./gradlew :otp-field:testDebugUnitTest   # Android unit tests (pure logic)
./gradlew :sample:desktopApp:run         # run the desktop sample
```

The input-processing logic (`processOtpInput`), the keyboard-type validators and the shake
keyframes are all pure functions in `src/commonTest`-tested code — prefer adding logic there so
it can be unit-tested without composition. UI tests use `compose.uiTest` and live in
`src/skikoTest`.

## Conventions

- Public API gets KDoc.
- Use `kotlin.time.Duration` rather than `java.time`.
- Add or update tests for every behaviour change — focus-management and paste behaviour in
  particular must stay covered.
- Update the sample app when you change a public API.
- Add a `CHANGELOG.md` entry under `## Unreleased`.

## Submitting a PR

1. Fork and create a topic branch.
2. Make your change, with tests.
3. Make sure `./gradlew build` and `./gradlew allTests` pass.
4. Open a PR against `main` and fill in the PR template.

## Releasing

Releases are tag-driven: pushing a `v*` tag runs `.github/workflows/publish.yml`, which publishes
to Maven Central via the `com.vanniktech.maven.publish` plugin and creates a GitHub Release.
