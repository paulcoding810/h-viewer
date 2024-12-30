package com.paulcoding.hviewer.ui.page.lock

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.paulcoding.hviewer.R
import com.paulcoding.hviewer.helper.makeToast
import com.paulcoding.hviewer.preference.Preferences
import com.paulcoding.hviewer.ui.component.HOTP
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LockPage(onUnlocked: () -> Unit) {
    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        delay(300)
        focusRequester.requestFocus()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.lock)) },
            )
        }
    ) { paddings ->
        Box(
            modifier = Modifier
                .padding(paddings)
                .fillMaxSize()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(stringResource(R.string.enter_your_pin))
                Spacer(modifier = Modifier.height(12.dp))
                HOTP(modifier = Modifier.focusRequester(focusRequester)) {
                    if (it == Preferences.pin) {
                        onUnlocked()
                    } else {
                        makeToast(context.getString(R.string.wrong_pin))
                    }
                }
            }
        }
    }
}

