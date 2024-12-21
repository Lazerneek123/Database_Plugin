package com.example.demo.action

import com.example.demo.daoConfig.DAOCreate
import com.example.demo.element.CapitalizeFirstLetter
import com.example.demo.generator.GenCreateDAO
import com.example.demo.inputDialog.dao.InputDDAO
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

class ActCreateDAO : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val selectedFile = event.getData(PlatformDataKeys.VIRTUAL_FILE)
        if (selectedFile != null) {
            val project = event.project
            val directoryPath = selectedFile.path

            val sourceRoot = "/src/main/java/"
            val packagePathWithoutSourceRoot = directoryPath.substringAfter(sourceRoot)
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
        val inputDialog = InputDDAO(directoryPath, event)
        if (inputDialog.showAndGet()) {
            var fileName = inputDialog.getFileName()

            if (fileName.isNotEmpty()) {
                val name = "${CapitalizeFirstLetter().uppercaseChar(fileName)}.kt"
                ApplicationManager.getApplication().runWriteAction {
                    createKotlinFile(
                        project,
                        directoryPath,
                        packagePath,
                        name,
                        inputDialog,
                        event
                    )
                }
            } else {
                fileName = "NewDatabaseKotlinFile.kt"
                createKotlinFile(
                    project,
                    directoryPath,
                    packagePath,
                    fileName,
                    inputDialog,
                    event
                )
            }
        }
    }

    private fun Project.showNotification(message: String) {
        val groupId = "Database Plugin Notification Group"
        NotificationGroupManager.getInstance().getNotificationGroup(groupId)
            .createNotification("Database class", message, NotificationType.INFORMATION).notify(this)
    }

    private fun createKotlinFile(
        project: Project,
        directoryPath: String,
        packagePath: String,
        fileName: String,
        inputDialog: InputDDAO,
        event: AnActionEvent
    ) {
        try {
            WriteCommandAction.runWriteCommandAction(project) {
                val directory = LocalFileSystem.getInstance().findFileByPath(directoryPath)
                val file = directory?.createChildData(this, fileName)

                val content = GenCreateDAO(
                    DAOCreate(
                        packagePath,
                        inputDialog.getPathChooseFile(),
                        inputDialog.getFileName(),
                        inputDialog.getNameChooseFile(),
                        inputDialog.getListQuery()
                    )
                ).generate()

                event.project?.showNotification(inputDialog.message)


                file?.setBinaryContent(content.toByteArray())

                event.project?.showNotification("The class ${inputDialog.getFileName()} is successfully created!")
                openFileInEditor(project, file!!)
            }


        } catch (e: IOException) {
            e.printStackTrace()
            event.project?.showNotification(e.message!!)
        }
    }

    private fun openFileInEditor(project: Project, file: VirtualFile) {
        val openFileDescriptor = OpenFileDescriptor(project, file)
        FileEditorManager.getInstance(project).openTextEditor(openFileDescriptor, true)
    }
}