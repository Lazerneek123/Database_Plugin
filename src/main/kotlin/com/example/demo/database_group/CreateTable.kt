package com.example.demo.database_group

import com.example.demo.code_generator.CGCreateTable
import com.example.demo.inputdialog.InputDialogEntity
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import java.io.IOException
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.application.ApplicationManager

class CreateTable : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val selectedFile = event.getData(PlatformDataKeys.VIRTUAL_FILE)
        if (selectedFile != null) {
            // Get the project
            val project = event.project

            // Get the path to the directory in which to create a new file
            val directoryPath = selectedFile.path

            // Отримайте шлях до пакету, видаливши зайві частини шляху
            val sourceRoot = "/src/main/java/" // Або "/src/main/kotlin/" для проектів Kotlin
            val packagePathWithoutSourceRoot = directoryPath.substringAfter(sourceRoot)

            // Отримайте пакет з отриманого шляху
            val packagePath = packagePathWithoutSourceRoot.replace("/", ".")

            messageShowInputDialog(project!!, directoryPath, packagePath, event)
        }
    }

    private fun messageShowInputDialog(
        project: Project,
        directoryPath: String,
        packagePath: String,
        event: AnActionEvent
    ) {
        val inputDialog = InputDialogEntity()
        if (inputDialog.showAndGet()) {
            var fileName = inputDialog.getTableName()

            if (!fileName.isNullOrEmpty()) {
                // Create a new file with the entered name and extension .kt
                val name = "$fileName.kt"
                // Use runWriteAction to access the file system within a write-action
                ApplicationManager.getApplication().runWriteAction {
                    createKotlinFile(project, directoryPath, packagePath, name, inputDialog, event)
                }
            } else {
                fileName = "NewDatabaseKotlinFile.kt"
                createKotlinFile(project, directoryPath, packagePath, fileName, inputDialog, event)
            }
        }
    }

    private fun Project.showNotification(message: String) {
        val groupId = "Custom Notification Group"
        NotificationGroupManager.getInstance().getNotificationGroup(groupId)
            .createNotification("Database class", message, NotificationType.INFORMATION).notify(this)
    }

    private fun createKotlinFile(
        project: Project,
        directoryPath: String,
        packagePath: String,
        fileName: String,
        inputDialog: InputDialogEntity,
        event: AnActionEvent
    ) {
        try {
            // Use WriteCommandAction to perform write-action operations
            WriteCommandAction.runWriteCommandAction(project) {
                // Create a file
                val directory = LocalFileSystem.getInstance().findFileByPath(directoryPath)

                val file = directory?.createChildData(this, fileName)

                // Write the contents of the file (you can also use templates to generate code)
                val content = CGCreateTable().generate(
                    packagePath,
                    inputDialog.getTableName(),
                    inputDialog.getPrimaryKeyName(),
                    inputDialog.getPrimaryKeyAutoGenerate(),
                    inputDialog.getPrimaryKeyValue()
                )
                file?.setBinaryContent(content.toByteArray())

                event.project?.showNotification("The class is successfully created!")
                // Open a new file in a tab
                openFileInEditor(project, file!!)
            }


        } catch (e: IOException) {
            e.printStackTrace()
            event.project?.showNotification(e.message!!)
        }
    }

    private fun openFileInEditor(project: Project, file: VirtualFile) {
        // Open the file in a tab
        val openFileDescriptor = OpenFileDescriptor(project, file)
        FileEditorManager.getInstance(project).openTextEditor(openFileDescriptor, true)
    }
}