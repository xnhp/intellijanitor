package com.github.xnhp.intellijanitor.settings

import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable
import javax.swing.JComponent

class IntelliJanitorSettingsConfigurable : Configurable {
    private var settingsComponent: IntelliJanitorSettingsComponent? = null

    override fun getDisplayName(): String = "IntelliJanitor Settings"

    override fun getPreferredFocusedComponent(): JComponent? {
        return settingsComponent?.getPreferredFocusedComponent()
    }

    override fun createComponent(): JComponent {
        settingsComponent = IntelliJanitorSettingsComponent()
        return settingsComponent!!.getPanel()
    }

    override fun isModified(): Boolean {
        val settings = service<IntelliJanitorSettingsState>()
        return settingsComponent?.getTargetImlLocation() != settings.targetImlLocation
    }

    override fun apply() {
        val settings = service<IntelliJanitorSettingsState>()
        settings.targetImlLocation = settingsComponent?.getTargetImlLocation() ?: ""
    }

    override fun reset() {
        val settings = service<IntelliJanitorSettingsState>()
        settingsComponent?.setTargetImlLocation(settings.targetImlLocation)
    }

    override fun disposeUIResources() {
        settingsComponent = null
    }
}
