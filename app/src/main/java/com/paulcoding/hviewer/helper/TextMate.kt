package com.paulcoding.hviewer.helper

import com.paulcoding.hviewer.MainApp.Companion.appContext
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver
import org.eclipse.tm4e.core.registry.IThemeSource

fun setupTextmate() {
    FileProviderRegistry.getInstance().addFileProvider(
        AssetsFileResolver(
            appContext.assets
        )
    )
    loadDefaultThemes()
    loadDefaultLanguages()
}

private fun loadDefaultThemes() {
    val themes = arrayOf("darcula", "abyss", "quietlight", "solarized_drak")
    val themeRegistry = ThemeRegistry.getInstance()
    themes.forEach { name ->
        val path = "textmate/$name.json"
        themeRegistry.loadTheme(
            ThemeModel(
                IThemeSource.fromInputStream(
                    FileProviderRegistry.getInstance().tryGetInputStream(path), path, null
                ), name
            ).apply {
                if (name != "quietlight") {
                    isDark = true
                }
            }
        )
    }

    themeRegistry.setTheme("quietlight")
}

private fun loadDefaultLanguages() {
    GrammarRegistry.getInstance().loadGrammars("textmate/languages.json")
}