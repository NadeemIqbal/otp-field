package io.github.nadeemiqbal.otpfield.sample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import io.github.nadeemiqbal.otpfield.OtpField
import io.github.nadeemiqbal.otpfield.OtpFieldStyle
import kotlin.time.Duration.Companion.milliseconds

private val styles = listOf(OtpFieldStyle.Boxed, OtpFieldStyle.Underlined, OtpFieldStyle.Rounded)
private val lengths = listOf(4, 6, 8)

/** Shared sample UI — tabbed by style, exercising every [OtpField] feature. */
@Composable
fun SampleApp() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            SampleContent()
        }
    }
}

@Composable
private fun SampleContent() {
    var tab by remember { mutableStateOf(0) }
    var lengthIndex by remember { mutableStateOf(1) } // default length 6
    var obscure by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    var value by remember { mutableStateOf("") }
    var lastCompleted by remember { mutableStateOf<String?>(null) }

    val clipboard = LocalClipboardManager.current
    val length = lengths[lengthIndex]
    val style = styles[tab]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text("OtpField", style = MaterialTheme.typography.headlineMedium)
        Text(
            "A polished segmented OTP / PIN input — with multiplatform clipboard paste.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        TabRow(selectedTabIndex = tab) {
            styles.forEachIndexed { index, style ->
                Tab(
                    selected = tab == index,
                    onClick = { tab = index; isError = false },
                    text = { Text(style.name) },
                )
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("Style: ${style.name}", style = MaterialTheme.typography.titleMedium)
                OtpField(
                    value = value,
                    onValueChange = { value = it; isError = false },
                    length = length,
                    onComplete = { lastCompleted = it },
                    style = style,
                    obscureChar = if (obscure) '•' else null,
                    obscureCharShownDelay = 500.milliseconds,
                    isError = isError,
                    errorMessage = if (isError) "Invalid code" else null,
                    modifier = Modifier.wrapContentWidth(Alignment.CenterHorizontally),
                )
                Text(
                    "Value: \"$value\"   ·   Completed: ${lastCompleted ?: "—"}",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        SectionCard("Controls") {
            LabeledControl("Length") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    lengths.forEachIndexed { index, len ->
                        FilterChip(
                            selected = lengthIndex == index,
                            onClick = { lengthIndex = index; value = ""; lastCompleted = null },
                            label = { Text("$len") },
                        )
                    }
                }
            }
            ToggleRow("Obscure (PIN mode)", obscure) { obscure = it }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { isError = true }) { Text("Trigger error") }
                OutlinedButton(
                    onClick = {
                        clipboard.setText(AnnotatedString("0123456789".take(length)))
                    },
                ) {
                    Text("Paste demo")
                }
            }
            Text(
                "\"Paste demo\" copies a $length-digit code to the clipboard — then long-press the " +
                    "field (or press Cmd/Ctrl+V on desktop) and paste: it fills every box at once.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            content()
        }
    }
}

@Composable
private fun LabeledControl(label: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        content()
    }
}

@Composable
private fun ToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
