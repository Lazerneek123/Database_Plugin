package com.example.demo.input

import com.example.demo.element.CapitalizeFirstLetter
import com.example.demo.generator.CreateRelationOneToOne
import com.example.demo.generator.entityRelation
import com.intellij.icons.AllIcons
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.IconUtil
import com.intellij.util.ui.JBUI
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.File
import java.io.IOException
import javax.swing.BoxLayout
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class InputDialogChooseRelation(
    private val directoryPath: String, private val packagePath: String, private val project: Project
) : DialogWrapper(true) {
    private val panel = JPanel()
    private val nameLabel = JLabel("Choose Relation")

    init {
        title = "Creating A Relationship Between Tables"


        init()

        panelRelationTable()
        isOKActionEnabled = true
    }

    private fun panelRelationTable() {

        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

        val relationTablePanel = JPanel()

        // Create a GridBagConstraints object to set the alignment
        val constraints = GridBagConstraints()
        constraints.gridx = 0
        constraints.gridy = 0
        constraints.fill = GridBagConstraints.CENTER
        constraints.insets = JBUI.insets(1) // Відступи

        // Uploading an image
        val icon = AllIcons.General.Tree

        // Change the size of the icon
        val scaledIcon = IconUtil.scale(icon, 5.0)

        val relationPanelOneToOne = JPanel(GridBagLayout())
        // Create a label for the image
        val labelRelationIcon1 = JLabel(scaledIcon)
        labelRelationIcon1.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                val inputDialogRelation = InputDialogRelationOneToOne(directoryPath, packagePath)
                inputDialogRelation.show()

                // Get the results when you click the OK button
                if (inputDialogRelation.isOK) {
                    // Use runWriteAction to access the file system within a write-action
                    ApplicationManager.getApplication().runWriteAction {
                        createKotlinFiles(inputDialogRelation, inputDialogRelation.getPathFile2())
                    }
                }
            }
        })
        val labelRelation1 = JLabel("1 : 1")
        relationPanelOneToOne.add(labelRelationIcon1, constraints)
        constraints.gridy++
        relationPanelOneToOne.add(labelRelation1, constraints)
        constraints.gridy--

        val relationPanelOneToMany = JPanel(GridBagLayout())
        val labelRelationIcon2 = JLabel(scaledIcon)
        labelRelationIcon2.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {

            }
        })
        val labelRelation2 = JLabel("1 : M")
        relationPanelOneToMany.add(labelRelationIcon2, constraints)
        constraints.gridy++
        relationPanelOneToMany.add(labelRelation2, constraints)
        constraints.gridy--

        val relationPanelManyToMany = JPanel(GridBagLayout())
        val labelRelationIcon3 = JLabel(scaledIcon)
        labelRelationIcon3.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                val inputDialogRelation = InputDialogRelationManyToMany()
                inputDialogRelation.show()

                if (inputDialogRelation.isOK) {

                }
            }
        })
        val labelRelation3 = JLabel("M : M")
        relationPanelManyToMany.add(labelRelationIcon3, constraints)
        constraints.gridy++
        relationPanelManyToMany.add(labelRelation3, constraints)
        constraints.gridy--

        val nameTablePanel = JPanel()
        nameTablePanel.layout = BoxLayout(nameTablePanel, BoxLayout.X_AXIS)

        relationTablePanel.add(nameLabel)
        nameTablePanel.add(relationPanelOneToOne)
        nameTablePanel.add(relationPanelOneToMany)
        nameTablePanel.add(relationPanelManyToMany)
        panel.add(relationTablePanel)
        panel.add(nameTablePanel)
    }

    private fun createKotlinFiles(
        inputDialog: InputDialogRelationOneToOne, pathFile: String
    ) {
        try {
            // A separate action for creating a new file
            WriteCommandAction.runWriteCommandAction(project) {

                val directory = LocalFileSystem.getInstance().findFileByPath(this.directoryPath)
                val name = "${CapitalizeFirstLetter().uppercaseChar(inputDialog.getRelationName())}.kt"
                val file = directory?.createChildData(this, name)

                if (file != null) {
                    val createRelationOneToOne = CreateRelationOneToOne()
                    val code = createRelationOneToOne.generate(
                        packagePath,
                        inputDialog.getTablePackagePath1(),
                        inputDialog.getTablePackagePath2(),
                        inputDialog.getTableName1(),
                        inputDialog.getTableName2(),
                        inputDialog.getParentColumn(),
                        inputDialog.getEntityColumn()
                    )
                    file.setBinaryContent(code.toByteArray())
                    showNotification("The class ${createRelationOneToOne.getNameClass()} is successfully created!")
                    openFileInEditor(project, file)
                } else {
                    showNotification("Unable to create the file. It might already exist.")
                }
            }

            // A separate action for editing an existing file
            WriteCommandAction.runWriteCommandAction(project) {
                val selectedFile = File(pathFile)
                if (selectedFile.exists()) {
                    var replacementCode = selectedFile.readText()

                    val entityPattern = """@Entity\(tableName = "${inputDialog.getTableName2()}"\)""".toRegex()
                    val newEntityAnnotation = entityRelation(inputDialog)

                    replacementCode = replacementCode.replace(entityPattern, newEntityAnnotation)

                    // Check if the ForeignKey import already exists, and add it if not
                    val importForeignKey = "import androidx.room.ForeignKey"
                    if (!replacementCode.contains(importForeignKey)) {
                        // We add the import after other imports
                        val importPosition = replacementCode.indexOf("import androidx.room.Entity")
                        if (importPosition != -1) {
                            val beforeImports = replacementCode.substring(0, importPosition)
                            val afterImports = replacementCode.substring(importPosition)
                            replacementCode = beforeImports + "$importForeignKey\n" + afterImports
                        }
                    }
                    // Overwrite the file with new content
                    selectedFile.writeText(replacementCode)
                    showNotification("The selected ${inputDialog.getClassName2()} file has been successfully updated!")
                } else {
                    showNotification("Selected file does not exist.")
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            showNotification(e.message ?: "An error occurred.")
        }
    }

    private fun showNotification(message: String) {
        val groupId = "Custom Notification Group"
        NotificationGroupManager.getInstance().getNotificationGroup(groupId)
            .createNotification("Database class", message, NotificationType.INFORMATION).notify(project)
    }

    private fun openFileInEditor(project: Project, file: VirtualFile) {
        // Open the file in a tab
        val openFileDescriptor = OpenFileDescriptor(project, file)
        FileEditorManager.getInstance(project).openTextEditor(openFileDescriptor, true)
    }

    override fun createCenterPanel(): JComponent {
        return panel
    }
}