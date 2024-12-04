package com.paulcoding.hviewer.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.paulcoding.hviewer.preference.Preferences

@Composable
fun HOTP(
    modifier: Modifier = Modifier,
    pinCount: Int = Preferences.pinCount,
    onCompleted: (String) -> Unit = {}
) {
    var text by remember { mutableStateOf("") }
    val localKeyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(text) {
        if (text.length == Preferences.pinCount) {
            localKeyboardController?.hide()
            onCompleted(text)
        }
    }

    BasicTextField(
        text,
        modifier = modifier,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        onValueChange = { if (it.length <= pinCount) text = it },
        decorationBox = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(pinCount) { index ->
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .border(1.dp, Color.Black, shape = RoundedCornerShape(4.dp))
                    ) {
                        Text(
                            modifier = Modifier.align(Alignment.Center),
                            text = text.getOrElse(index) { ' ' }.toString()
                        )
                    }
                }
            }
        })
}

