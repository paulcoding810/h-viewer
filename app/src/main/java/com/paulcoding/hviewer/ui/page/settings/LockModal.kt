package com.paulcoding.hviewer.ui.page.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.paulcoding.hviewer.ui.component.HOTP

@Composable
fun LockModal(onDismiss: () -> Unit, onPinConfirmed: (String) -> Unit = {}) {
    var pin by remember { mutableStateOf("") }

    fun dismiss() {
        onDismiss()
    }

    Dialog(onDismissRequest = { dismiss() }) {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Enter New Pin")
                HOTP {
                    pin = it
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.align(Alignment.End)) {
                    TextButton(onClick = { dismiss() }) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    TextButton(onClick = { onPinConfirmed(pin) }) {
                        Text("OK")
                    }
                }
            }
        }
    }
}

