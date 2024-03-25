package com.example.demo

import com.intellij.notification.*
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project

class Main : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        event.project?.showNotification("Plugin installed successfully!")
    }

    private fun Project.showNotification(message: String) {
        val groupId = "Custom Notification Group"
        NotificationGroupManager.getInstance().getNotificationGroup(groupId)
            .createNotification("Database plugin", message, NotificationType.INFORMATION)
            .notify(this)
    }
}