package io.github.nadeemiqbal.otpfield.sample.desktop

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.github.nadeemiqbal.otpfield.sample.SampleApp

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "OtpField Sample",
        state = rememberWindowState(width = 480.dp, height = 800.dp),
    ) {
        SampleApp()
    }
}
