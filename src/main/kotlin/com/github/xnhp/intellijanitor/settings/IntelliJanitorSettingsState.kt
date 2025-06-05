package com.github.xnhp.intellijanitor.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@Service(Service.Level.APP)
@State(
    name = "com.github.xnhp.intellijanitor.settings.IntelliJanitorSettingsState",
    storages = [Storage("IntelliJanitorSettings.xml")]
)
class IntelliJanitorSettingsState : PersistentStateComponent<IntelliJanitorSettingsState> {
    var targetImlLocation: String = ""

    override fun getState(): IntelliJanitorSettingsState = this

    override fun loadState(state: IntelliJanitorSettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }
}
