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

    private val _effect = MutableStateFlow<Effect?>(null)
    val effect = _effect.asStateFlow()

    init {
        siteConfigsRepository.siteConfigs.onEach { configs -> setConfigs(configs) }.launchIn(viewModelScope)
        loadSites()
    }

    fun dispatch(action: Actions) {
        when (action) {
            is Actions.LoadSites -> loadSites()
            is Actions.ConsumeEffect -> {
                _effect.value = null
            }
        }
    }

    private fun loadSites() {
        viewModelScope.launch {
            val localConfigs = _uiState.value.siteConfigs ?: return@launch

            if (BuildConfig.DEBUG) return@launch

            val remoteUrl = siteConfigsRepository.remoteUrl
            val branch = siteConfigsRepository.branch

            siteConfigsRepository.getRemoteConfigs(remoteUrl, branch)
                .onSuccess { remoteConfigs ->
                    if (remoteConfigs.version > localConfigs.version) {
                        updateConfigs(remoteConfigs)
                    }
                }
        }
    }

    private fun updateConfigs(remoteConfigs: SiteConfigs) {
        viewModelScope.launch {
            siteConfigsRepository.saveRemoteScripts(remoteConfigs)
                .onSuccess {
                    _effect.value = Effect.UpdatedConfigs(remoteConfigs.version)
                    println("🚀 ~ UpdatedConfigs")
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

    sealed class Effect {
        data class UpdatedConfigs(val version: Int) : Effect()
    }

    sealed class Actions {
        object LoadSites : Actions()
        object ConsumeEffect : Actions()
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