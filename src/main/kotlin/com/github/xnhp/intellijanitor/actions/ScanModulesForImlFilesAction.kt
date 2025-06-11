package com.github.xnhp.intellijanitor.actions

import com.github.xnhp.intellijanitor.MyBundle
import com.github.xnhp.intellijanitor.settings.IntelliJanitorSettingsState
import com.github.xnhp.intellijanitor.utils.ImlFileUtils
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbAware

/**
 * Action to scan project modules for IML files and offer to move them to the target location.
 */
class ScanModulesForImlFilesAction : AnAction(), DumbAware {

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val settingsState = service<IntelliJanitorSettingsState>()
        val targetPath = settingsState.targetImlLocation

        if (targetPath.isEmpty()) {
            NotificationGroupManager.getInstance()
                .getNotificationGroup("IntelliJanitor Notifications")
                .createNotification(
                    MyBundle.message("action.scanModules.title"),
                    "Please set a target location for IML files in Settings | Tools | IntelliJanitor Settings",
                    NotificationType.WARNING
                ).notify(project)
            return
        }

        // Find IML files for modules
        val moduleImlFiles = ImlFileUtils.findModuleImlFiles(project)

        if (moduleImlFiles.isEmpty()) {
            NotificationGroupManager.getInstance()
                .getNotificationGroup("IntelliJanitor Notifications")
                .createNotification(
                    MyBundle.message("action.scanModules.title"),
                    MyBundle.message("action.scanModules.noImlFiles"),
                    NotificationType.INFORMATION
                ).notify(project)
            return
        }

        // Track if any IML files were found that need moving
        var foundFilesToMove = false

        // For each IML file found, only show notification if it's not already in target directory
        moduleImlFiles.forEach { (module, imlFile) ->
            if (imlFile != null && !ImlFileUtils.isImlFileInTargetDirectory(imlFile, targetPath)) {
                foundFilesToMove = true
                ImlFileUtils.showMoveImlNotification(
                    project,
                    imlFile,
                    MyBundle.message("action.scanModules.imlFileFound.title"),
                    MyBundle.message("action.scanModules.imlFileFound.content", module.name, imlFile.path),
                    targetPath
                )
            }
        }

        // If no files were found that need moving, show a message
        if (!foundFilesToMove && moduleImlFiles.isNotEmpty()) {
            NotificationGroupManager.getInstance()
                .getNotificationGroup("IntelliJanitor Notifications")
                .createNotification(
                    MyBundle.message("action.scanModules.title"),
                    MyBundle.message("action.scanModules.allInTarget"),
                    NotificationType.INFORMATION
                ).notify(project)
        }
    }
}
