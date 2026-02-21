package com.paulcoding.hviewer.ui.page.lock

import androidx.lifecycle.ViewModel
import com.paulcoding.hviewer.preference.Preferences

class LockViewModel(preferences: Preferences): ViewModel() {
    val pin = preferences.pin
}