package com.github.xnhp.intellijanitor.settings

import com.github.xnhp.intellijanitor.MyBundle
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import javax.swing.JComponent
import javax.swing.JPanel

class IntelliJanitorSettingsComponent {
    private val mainPanel: JPanel
    private val targetImlLocationField = TextFieldWithBrowseButton()

    init {
        targetImlLocationField.addBrowseFolderListener(
            MyBundle.message("settings.targetImlLocation.chooser.title"),
            MyBundle.message("settings.targetImlLocation.chooser.description"),
            null,
            FileChooserDescriptorFactory.createSingleFolderDescriptor()
        )

        // Create description label with proper styling
        val descriptionLabel = JBLabel(MyBundle.message("settings.targetImlLocation.description"))
        descriptionLabel.componentStyle = UIUtil.ComponentStyle.SMALL
        descriptionLabel.foreground = JBUI.CurrentTheme.ContextHelp.FOREGROUND
        descriptionLabel.border = JBUI.Borders.empty(2, 28, 0, 0)

        mainPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel(MyBundle.message("settings.targetImlLocation.label")), targetImlLocationField, 1, false)
            .addComponent(descriptionLabel)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    fun getPanel(): JComponent = mainPanel

    fun getPreferredFocusedComponent(): JComponent = targetImlLocationField

    fun getTargetImlLocation(): String = targetImlLocationField.text

    fun setTargetImlLocation(location: String) {
        targetImlLocationField.text = location
    }
}
