package com.github.xnhp.intellijanitor.startup

import com.github.xnhp.intellijanitor.listeners.ImlFileListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.util.messages.MessageBusConnection

class ImlFileListenerRegistration : ProjectActivity {
    override suspend fun execute(project: Project) {
        val connection: MessageBusConnection = project.messageBus.connect()
        connection.subscribe(VirtualFileManager.VFS_CHANGES, ImlFileListener(project))
    }
}
