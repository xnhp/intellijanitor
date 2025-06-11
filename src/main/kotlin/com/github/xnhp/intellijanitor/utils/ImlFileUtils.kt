package com.github.xnhp.intellijanitor.utils

import com.github.xnhp.intellijanitor.MyBundle
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.refactoring.move.moveFilesOrDirectories.MoveFilesOrDirectoriesProcessor
import java.io.File

/**
 * Utility class for operations related to IML files.
 */
object ImlFileUtils {

    /**
     * Checks if the IML file corresponds to a module in the project.
     * An IML file matches a module if its name (without extension) matches the module name.
     *
     * @param project The project containing the modules
     * @param imlFile The IML file to check
     * @return true if the IML file matches a module, false otherwise
     */
    fun isModuleMatchingImlFile(project: Project, imlFile: VirtualFile): Boolean {
        val moduleManager = ModuleManager.getInstance(project)
        val modules = moduleManager.modules
        val imlFileName = imlFile.nameWithoutExtension

        // Check if the IML file name matches any module name
        return modules.any { module ->
            val moduleName = module.name
            moduleName == imlFileName
        }
    }

    /**
     * Checks if the IML file is already in the target directory.
     * 
     * @param imlFile The IML file to check
     * @param targetPath The target directory path
     * @return true if the file is already in the target directory, false otherwise
     */
    fun isImlFileInTargetDirectory(imlFile: VirtualFile, targetPath: String): Boolean {
        val targetDir = LocalFileSystem.getInstance().findFileByPath(targetPath) ?: return false
        val imlFileParent = imlFile.parent

        return imlFileParent != null && imlFileParent.path == targetDir.path
    }

    /**
     * Displays a notification for moving an IML file to the target location.
     * Only shows the move action if the file is not already in the target directory.
     * 
     * @param project The current project
     * @param imlFile The IML file to move
     * @param title The notification title
     * @param content The notification content
     * @param targetPath The target path to move the file to
     */
    fun showMoveImlNotification(project: Project, imlFile: VirtualFile, title: String, content: String, targetPath: String) {
        val notificationGroup = NotificationGroupManager.getInstance().getNotificationGroup("IntelliJanitor Notifications")
        val notification = notificationGroup.createNotification(
            title,
            content,
            NotificationType.INFORMATION
        )

        // Only add the move action if the file is not already in the target directory
        if (!isImlFileInTargetDirectory(imlFile, targetPath)) {
            notification.addAction(object : AnAction(MyBundle.message("imlFileCreated.move")) {
                override fun actionPerformed(e: AnActionEvent) {
                    moveImlFile(project, imlFile, targetPath)
                }
            })
        }

        notification.notify(project)
    }

    /**
     * Moves an IML file to the specified target path.
     * 
     * @param project The current project
     * @param imlFile The IML file to move
     * @param targetPath The target path to move the file to
     */
    fun moveImlFile(project: Project, imlFile: VirtualFile, targetPath: String) {
        val targetDir = LocalFileSystem.getInstance().findFileByPath(targetPath)
        if (targetDir != null) {
            val psiManager = PsiManager.getInstance(project)
            val psiFile = psiManager.findFile(imlFile) ?: return
            val targetPsiDir = psiManager.findDirectory(targetDir) ?: return

            MoveFilesOrDirectoriesProcessor(
                project,
                arrayOf(psiFile),
                targetPsiDir,
                false,  // search in comments
                false,  // search in non-java files
                null,
                null
            ).run()
        }
    }

    /**
     * Finds IML files for modules in the project.
     * 
     * @param project The current project
     * @return Map of modules to their corresponding IML files (if found)
     */
    fun findModuleImlFiles(project: Project): Map<Module, VirtualFile?> {
        val moduleManager = ModuleManager.getInstance(project)
        val modules = moduleManager.modules
        val result = mutableMapOf<Module, VirtualFile?>()

        for (module in modules) {
            val moduleFilePath = module.moduleFilePath
            val moduleFile = File(moduleFilePath)

            if (moduleFile.exists() && moduleFile.extension == "iml") {
                val virtualFile = LocalFileSystem.getInstance().findFileByIoFile(moduleFile)
                result[module] = virtualFile
            }
        }

        return result
    }
}
