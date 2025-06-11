package com.github.xnhp.intellijanitor.listeners

import com.github.xnhp.intellijanitor.MyBundle
import com.github.xnhp.intellijanitor.settings.IntelliJanitorSettingsState
import com.github.xnhp.intellijanitor.utils.ImlFileUtils
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileCreateEvent
import com.intellij.openapi.vfs.newvfs.events.VFileEvent

class ImlFileListener(private val project: Project) : BulkFileListener {
    override fun after(events: List<VFileEvent>) {
        for (event in events) {
            if (event is VFileCreateEvent && event.file?.extension == "iml") {
                val imlFile = event.file ?: continue

                // Check if the IML file matches a module in the project
                if (ImlFileUtils.isModuleMatchingImlFile(project, imlFile)) {
                    val settingsState = service<IntelliJanitorSettingsState>()
                    val targetPath = settingsState.targetImlLocation

                    if (targetPath.isNotEmpty() && !ImlFileUtils.isImlFileInTargetDirectory(imlFile, targetPath)) {
                        ImlFileUtils.showMoveImlNotification(
                            project,
                            imlFile,
                            MyBundle.message("imlFileCreated.title"),
                            MyBundle.message("imlFileCreated.content", imlFile.name, imlFile.path),
                            targetPath
                        )
                    }
                }
            }
        }
    }
}
