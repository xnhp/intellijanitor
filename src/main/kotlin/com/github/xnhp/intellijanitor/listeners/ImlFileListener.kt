package com.github.xnhp.intellijanitor.listeners

import com.github.xnhp.intellijanitor.MyBundle
import com.github.xnhp.intellijanitor.settings.IntelliJanitorSettingsState
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileCreateEvent
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.psi.PsiManager
import com.intellij.refactoring.move.moveFilesOrDirectories.MoveFilesOrDirectoriesProcessor

class ImlFileListener(private val project: Project) : BulkFileListener {
    override fun after(events: List<VFileEvent>) {
        for (event in events) {
            if (event is VFileCreateEvent && event.file?.extension == "iml") {
                val imlFile = event.file ?: continue

                // Check if the IML file matches a module in the project
                if (isModuleMatchingImlFile(imlFile)) {
                    val settingsState = service<IntelliJanitorSettingsState>()
                    val targetPath = settingsState.targetImlLocation

                    if (targetPath.isNotEmpty()) {
                        showMoveImlNotification(project, imlFile, targetPath)
                    }
                }
            }
        }
    }

    private fun showMoveImlNotification(project: Project, imlFile: VirtualFile, targetPath: String) {
        val notificationGroup = NotificationGroupManager.getInstance().getNotificationGroup("IntelliJanitor Notifications")

        notificationGroup.createNotification(
            MyBundle.message("imlFileCreated.title"),
            MyBundle.message("imlFileCreated.content", imlFile.name, imlFile.path),
            NotificationType.INFORMATION
        ).addAction(object : AnAction(MyBundle.message("imlFileCreated.move")) {
            override fun actionPerformed(e: AnActionEvent) {
                moveImlFile(project, imlFile, targetPath)
            }
        }).notify(project)
    }

    private fun moveImlFile(project: Project, imlFile: VirtualFile, targetPath: String) {
        val targetDir = LocalFileSystem.getInstance().findFileByPath(targetPath)
        if (targetDir != null) {
            val psiManager = PsiManager.getInstance(project)
            val psiFile = psiManager.findFile(imlFile) ?: return
            val targetPsiDir = psiManager.findDirectory(targetDir) ?: return

            MoveFilesOrDirectoriesProcessor(
                project,
                arrayOf(psiFile),
                targetPsiDir,
                false,
                true,
                null,
                null
            ).run()
        }
    }

    /**
     * Checks if the IML file corresponds to a module in the project.
     * An IML file matches a module if its name (without extension) matches the module name.
     *
     * @param imlFile The IML file to check
     * @return true if the IML file matches a module, false otherwise
     */
    private fun isModuleMatchingImlFile(imlFile: VirtualFile): Boolean {
        val moduleManager = ModuleManager.getInstance(project)
        val modules = moduleManager.modules
        val imlFileName = imlFile.nameWithoutExtension

        // Check if the IML file name matches any module name
        return modules.any { module ->
            val moduleName = module.name
            moduleName == imlFileName
        }
    }
}
