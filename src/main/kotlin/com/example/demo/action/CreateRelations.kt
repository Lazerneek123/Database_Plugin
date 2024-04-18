package com.example.demo.action

import com.example.demo.input.InputDialogChooseRelation
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.project.Project

class CreateRelations : AnAction() {
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

            messageShowInputDialog(project!!, directoryPath, packagePath)
        }
    }

    private fun messageShowInputDialog(
        project: Project,
        directoryPath: String,
        packagePath: String
    ) {
        val inputDialog = InputDialogChooseRelation(directoryPath, packagePath, project)
        inputDialog.show()
    }
}