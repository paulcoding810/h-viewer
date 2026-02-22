package com.paulcoding.hviewer.ui.page.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulcoding.hviewer.APK_NAME
import com.paulcoding.hviewer.BuildConfig
import com.paulcoding.hviewer.model.HRelease
import com.paulcoding.hviewer.preference.Preferences
import com.paulcoding.hviewer.repository.SiteConfigsRepository
import com.paulcoding.hviewer.repository.UpdateAppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class SettingsViewModel(
    private val context: Context,
    private val preferences: Preferences,
    private val configsRepository: SiteConfigsRepository,
    private val updateAppRepository: UpdateAppRepository
) : ViewModel() {
    var checkedForUpdateAtLaunch = false

    private var _uiState = MutableStateFlow(
        UiState(
            remoteUrl = preferences.remoteUrl.ifEmpty { "https://github.com/{OWNER}/{REPO}/" },
            remoteBranch = preferences.branch,
            isLockScreenEnabled = preferences.pin.isNotEmpty(),
            isDevMode = false,
            isSecureScreenEnabled = preferences.secureScreen,
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _effect = MutableStateFlow<Effect?>(null)
    val effect = _effect.asStateFlow()

    fun dispatch(action: Action) {
        when (action) {
            is Action.Unlock -> unlock()
            is Action.SetPin -> setPin(action.pin)
            is Action.DisableLock -> disableLock()
            is Action.SetDevMode -> setDevMode(action.isDevMode)
            is Action.SetSecureScreen -> setSecureScreen(action.enabled)
            is Action.UpdateRemoteUrl -> updateRemoteUrl(action.url, action.branch)
            is Action.ConsumeEffect -> consumeEffect(action.effect)
            is Action.CheckForAppUpdate -> checkForAppUpdate()
            is Action.SetEditPinModalVisible -> setEditPinModalVisible(action.visible)
            is Action.DismissAppUpdate -> dismissAppUpdate()
            is Action.DownloadAndInstallApk -> downloadAndInstallApk()
            is Action.ToggleRemoteModal -> setRemoteModalVisible(action.visible)

        }
    }

    private fun setRemoteModalVisible(visible: Boolean) {
        _uiState.update {
            it.copy(showRemoteModal = visible)
        }
    }

    private fun setEditPinModalVisible(visible: Boolean) {
        _uiState.update {
            it.copy(showOinModal = visible)
        }
    }

    private fun downloadAndInstallApk() {
        val newRelease = _uiState.value.newRelease ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true) }
            updateAppRepository.downloadApk(
                newRelease.downloadUrl,
                destination = File(context.cacheDir, APK_NAME)
            ).onSuccess { file ->
                val fileUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                installApk(context, fileUri)
                _uiState.update { it.copy(isUpdating = false) }
            }.onFailure { exception ->
                exception.printStackTrace()
                _effect.value = Effect.Toast("Failed to download update")
                _uiState.update { it.copy(isUpdating = false) }
            }
        }
    }

    private fun installApk(context: Context, uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        context.startActivity(intent)
    }

    private fun dismissAppUpdate() {
        _uiState.update { it.copy(newRelease = null) }
    }

    private fun checkForAppUpdate() {
        viewModelScope.launch {
            updateAppRepository.getLatestAppRelease()
                .onSuccess { release ->
                    checkedForUpdateAtLaunch = true

                    if (release.version != BuildConfig.VERSION_NAME) {
                        _uiState.update { it.copy(newRelease = release) }
                    } else {
                        _effect.value = Effect.Toast("App is up to date")
                    }
                }
        }
    }

    private fun consumeEffect(effect: Effect?) {
        _effect.value = null
    }

    private fun updateRemoteUrl(url: String, branch: String) {
        viewModelScope.launch {
            configsRepository.getRemoteConfigs(url, branch)
                .onSuccess { siteConfigs ->
                    preferences.branch = branch
                    preferences.remoteUrl = url
                    configsRepository.saveRemoteScripts(remoteConfigs = siteConfigs)
                    _effect.value = Effect.Toast("Remote config updated")
                }
                .onFailure { exception ->
                    exception.printStackTrace()
                    _effect.value = Effect.Toast("Failed to fetch configs")
                }
        }
    }

    private fun disableLock() {
        preferences.pin = ""
        unlock()
    }

    private fun unlock() {
        _uiState.update { it.copy(isLockScreenEnabled = false) }
    }

    private fun setPin(pin: String) {
        preferences.pin = pin
        _uiState.update { it.copy(isLockScreenEnabled = true) }
        _uiState.update { it.copy(showOinModal = false) }
    }

    private fun setDevMode(on: Boolean) {
        _uiState.update { it.copy(isDevMode = on) }
    }

    private fun setSecureScreen(enabled: Boolean) {
        _uiState.update { it.copy(isSecureScreenEnabled = enabled) }
        preferences.secureScreen = enabled
    }

    data class UiState(
        val isLockScreenEnabled: Boolean,
        val showOinModal: Boolean = false,
        val showRemoteModal: Boolean = false,
        val isDevMode: Boolean,
        val isSecureScreenEnabled: Boolean,
        val remoteUrl: String,
        val remoteBranch: String,
        val newRelease: HRelease? = null,
        val isUpdating: Boolean = false,
        val isUpdateSuccess: Boolean = false,
    )

    sealed class Action {
        data object Unlock : Action()
        data object DisableLock : Action()
        data class SetPin(val pin: String) : Action()
        data class SetDevMode(val isDevMode: Boolean) : Action()
        data class SetSecureScreen(val enabled: Boolean) : Action()
        data class UpdateRemoteUrl(val url: String, val branch: String) : Action()
        data class CheckForAppUpdate(val showToast: Boolean = false) : Action()
        data object DismissAppUpdate : Action()
        data object DownloadAndInstallApk : Action()
        data class ToggleRemoteModal(val visible: Boolean) : Action()
        data class SetEditPinModalVisible(val visible: Boolean) : Action()
        data class ConsumeEffect(val effect: Effect?) : Action()
    }

    sealed class Effect {
        data class Toast(val message: String) : Effect()
    }
}