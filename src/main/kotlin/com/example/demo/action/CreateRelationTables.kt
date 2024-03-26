package com.example.demo.action

import com.example.demo.element.CapitalizeFirstLetter
import com.example.demo.generator.CreateTableForRelation
import com.example.demo.generator.CreateTableRelation
import com.example.demo.input.InputDialogRelation
import com.example.demo.model.Column
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import java.io.IOException

class CreateRelationTables : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val selectedFile = event.getData(PlatformDataKeys.VIRTUAL_FILE)
        if (selectedFile != null) {
            // Get the project
            val project = event.project

            // Get the path to the directory in which to create a new file
            val directoryPath = selectedFile.path

            // Get the package path by removing the redundant parts of the path
            val sourceRoot = "/src/main/java/"
            val packagePathWithoutSourceRoot = directoryPath.substringAfter(sourceRoot)

            // Receive the packet from the received path
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
        val inputDialog = InputDialogRelation()
        if (inputDialog.showAndGet()) {
            // Use runWriteAction to access the file system within a write-action
            ApplicationManager.getApplication().runWriteAction {
                createKotlinFiles(project, directoryPath, packagePath, inputDialog, event)
            }
        }
    }

    private fun Project.showNotification(message: String) {
        val groupId = "Custom Notification Group"
        NotificationGroupManager.getInstance().getNotificationGroup(groupId)
            .createNotification("Database class", message, NotificationType.INFORMATION).notify(this)
    }

    private fun createKotlinFiles(
        project: Project,
        directoryPath: String,
        packagePath: String,
        inputDialog: InputDialogRelation,
        event: AnActionEvent
    ) {
        try {
            // Use WriteCommandAction to perform write-action operations
            WriteCommandAction.runWriteCommandAction(project) {
                // Create a file
                val directory = LocalFileSystem.getInstance().findFileByPath(directoryPath)
                // Create a new file with the entered name and extension .kt
                var name = "${CapitalizeFirstLetter().setString(inputDialog.getTableName1())}.kt"
                var file = directory?.createChildData(this, name)

                var createTableForRelation = CreateTableForRelation()
                var content = createTableForRelation.generate(
                    packagePath,
                    inputDialog.getTableName1(),
                    inputDialog.getTableName2(),
                    inputDialog.getTableNameRelation(),
                    inputDialog.getPrimaryKey1(),
                    inputDialog.getPrimaryKey2(),
                    listOf(Column("name2", "String", """"Roman"""", false))
                )

                file?.setBinaryContent(content.toByteArray())
                event.project?.showNotification("The class is successfully created!")
                // Open a new file in a tab
                openFileInEditor(project, file!!)

                name = "${CapitalizeFirstLetter().setString(inputDialog.getTableName2())}.kt"
                file = directory?.createChildData(this, name)

                createTableForRelation = CreateTableForRelation()
                content = createTableForRelation.generate(
                    packagePath,
                    inputDialog.getTableName2(),
                    inputDialog.getTableName1(),
                    inputDialog.getTableNameRelation(),
                    inputDialog.getPrimaryKey2(),
                    inputDialog.getPrimaryKey1(),
                    listOf(Column("name1", "String", """"Maks"""", false))
                )

                file?.setBinaryContent(content.toByteArray())
                event.project?.showNotification("The class is successfully created!")
                // Open a new file in a tab
                openFileInEditor(project, file!!)


                name = "${CapitalizeFirstLetter().setString(inputDialog.getTableNameRelation())}.kt"
                file = directory?.createChildData(this, name)
                val createTableRelation = CreateTableRelation()
                content = createTableRelation.generate(
                    packagePath,
                    inputDialog.getTableName1(),
                    inputDialog.getTableName2(),
                    inputDialog.getTableNameRelation(),
                    inputDialog.getPrimaryKey1(),
                    inputDialog.getPrimaryKey2()
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