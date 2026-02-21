package com.paulcoding.hviewer.ui.page.sites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulcoding.hviewer.BuildConfig
import com.paulcoding.hviewer.exception.AppException
import com.paulcoding.hviewer.model.SiteConfigs
import com.paulcoding.hviewer.repository.SiteConfigsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SitesViewModel(
    private val siteConfigsRepository: SiteConfigsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private var _remoteConfigs: SiteConfigs? = null

    init {
        siteConfigsRepository.siteConfigs.onEach { configs -> setConfigs(configs) }.launchIn(viewModelScope)
    }

    fun dispatch(action: Actions) {
        when (action) {
            is Actions.LoadSites -> loadSites()
            is Actions.UpdateConfigs -> updateConfigs()
        }
    }

    private fun loadSites() {
        //if (remoteUrl.isEmpty()) {
        //    throw (Exception("Remote url is empty"))
        //}
        //if (remoteUrl != preferences.getRemote()) {
        //    downloadAndGetConfig(remoteUrl)
        //    return@withContext SiteConfigsState.NewConfigsInstall(remoteUrl)
        //}

        //if (currentConfigs == null) {
        //    downloadAndGetConfig()
        //    return@withContext SiteConfigsState.NewConfigsInstall(remoteUrl)
        //} else {

        viewModelScope.launch {
            val localConfigs = _uiState.value.siteConfigs ?: return@launch

            if (BuildConfig.DEBUG) return@launch

            val remoteUrl = siteConfigsRepository.remoteUrl
            val branch = siteConfigsRepository.branch

            siteConfigsRepository.getRemoteConfigs(remoteUrl, branch)
                .onSuccess { remoteConfigs ->
                    _remoteConfigs = remoteConfigs
                    if (remoteConfigs.version > localConfigs.version) {
                        _uiState.update {
                            it.copy(
                                updateState = UpdateState(
                                    version = remoteConfigs.version,
                                )
                            )
                        }
                    }
                }
        }
    }

    private fun updateConfigs() {
        if (_remoteConfigs == null) return

        viewModelScope.launch {
            _uiState.update { it.copy(updateState = it.updateState?.copy(isLoading = true)) }
            siteConfigsRepository.saveRemoteScripts(_remoteConfigs!!)
                .onSuccess {
                    _uiState.update { it.copy(updateState = null) }
                    _remoteConfigs = null
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            updateState = it.updateState?.copy(
                                isLoading = false,
                                error = exception as? AppException
                            )
                        )
                    }
                }
        }
    }

    private fun setConfigs(siteConfigs: SiteConfigs?) {
        _uiState.update {
            it.copy(
                siteConfigs = siteConfigs
            )
        }
    }

    sealed class Actions {
        object LoadSites : Actions()
        object UpdateConfigs : Actions()
    }

    data class UiState(
        val siteConfigs: SiteConfigs? = null,
        val remoteUrl: String = "",
        val updateState: UpdateState? = null,
        val isLoading: Boolean = false,
    )

    data class UpdateState(
        val version: Int,
        val isLoading: Boolean = false,
        val isSuccess: Boolean = false,
        val error: AppException? = null
    )
}