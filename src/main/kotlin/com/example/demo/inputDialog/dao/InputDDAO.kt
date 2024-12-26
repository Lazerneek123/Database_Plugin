package com.example.demo.inputDialog.dao

import com.example.demo.element.TextFieldRegex
import com.example.demo.model.*
import com.intellij.icons.AllIcons
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.util.IconUtil
import com.intellij.util.ui.JBUI
import org.jetbrains.kotlin.idea.core.util.toVirtualFile
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.File
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class InputDDAO(
    private val directoryPath: String,
    private val event: AnActionEvent,
    private val project: Project
) : DialogWrapper(true) {
    val message: String = "Create sucsasfull"
    private val panelMain = JPanel()
    private var selectedFileName: String? = null
    private var selectedFilePathPackage: String? = null
    private var selectedPath: String? = null

    private val nameLabel = JLabel("Name:")
    private val nameTableField = JTextField()

    private val listModelQuery = DefaultListModel<Query>()
    private val listQuery = JList(listModelQuery)

    private val addBtnQuery = JButton("Add")
    private val removeBtnQuery = JButton("Delete")

    init {
        title = "Create DAO"
        init()
        isOKActionEnabled = false

        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.X_AXIS)
        panel.add(nameLabel)
        panel.add(nameTableField)
        panelMain.add(panel)

        panelChooseFile()

        panelMain.add(Box.createVerticalStrut(5))
        val panelColumn = JPanel()
        panelColumn.layout = BoxLayout(panelColumn, BoxLayout.X_AXIS)
        panelColumn.add(JLabel("Queries"))
        panelMain.add(panelColumn)
        panelMain.add(Box.createVerticalStrut(5))

        val panelQueries = JPanel()
        panelQueries.layout = BoxLayout(panelQueries, BoxLayout.X_AXIS)
        panelCreateQueries(panelQueries)
        panelMain.add(panelQueries)
    }

    private fun panelCreateQueries(panel: JPanel) {
        // Add button
        addBtnQuery.addActionListener {
            //val path = "${project.basePath}\\$selectedFileName.kt"
            val path = selectedPath.toString()

            //val path = selectedFile.path
            val virtualFile = LocalFileSystem.getInstance().findFileByPath(path)
            val psiFile = PsiManager.getInstance(project).findFile(virtualFile!!)
            val ktFile = psiFile as? KtFile

            val inputDialog = InputDQuery(listQuery, ktFile!!)
            inputDialog.show()

            // Get the results when you click the OK button
            if (inputDialog.isOK) {
                val name = inputDialog.getName()
                val queryType = inputDialog.getQueryCategory()
                val queryCategory = inputDialog.getQueryType()
                val valueQuery = inputDialog.getValueQuery()
                val onConflict = inputDialog.getOnConflict()
                val column = inputDialog.getColumnSelected()

                listModelQuery.addElement(
                    Query(
                        queryType,
                        queryCategory,
                        name,
                        null,
                        onConflict,
                        valueQuery,
                        selectedFilePathPackage!!,
                        selectedFileName!!,
                        ktFile,
                        column
                    )
                )
                checkConditions()
            }
        }

        // Delete button
        removeBtnQuery.addActionListener {
            val selectedIndex = listQuery.selectedIndex
            if (selectedIndex != -1) {
                listModelQuery.removeElementAt(selectedIndex)
                checkConditions()
            }
        }

        addBtnQuery.isEnabled = false
        removeBtnQuery.isEnabled = false


        // Adding a list to a panel
        panel.add(JScrollPane(listQuery), BorderLayout.CENTER)

        // Adding buttons to a panel
        val buttonPanel = JPanel()
        buttonPanel.layout = BoxLayout(buttonPanel, BoxLayout.Y_AXIS)
        buttonPanel.add(addBtnQuery)
        buttonPanel.add(removeBtnQuery)
        panel.add(buttonPanel, BorderLayout.SOUTH)
    }

    private fun panelChooseFile() {
        panelMain.layout = BoxLayout(panelMain, BoxLayout.Y_AXIS)

        val tablePanel = JPanel()

        // Create a GridBagConstraints object to set the alignment
        val constraints = GridBagConstraints()
        constraints.gridx = 0
        constraints.gridy = 0
        constraints.fill = GridBagConstraints.CENTER
        constraints.insets = JBUI.insets(1)

        // Uploading an image
        val icon = AllIcons.General.OpenDisk

        // Change the size of the icon
        val scaledIcon = IconUtil.scale(icon, 5.0)

        val chooseFilePane = JPanel(GridBagLayout())
        // Create a label for the image
        val labelIcon = JLabel(scaledIcon)
        val labelFile = JLabel("File Name:")
        labelIcon.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                fileChooser(labelFile)
            }
        })

        chooseFilePane.add(labelIcon, constraints)
        constraints.gridy++
        chooseFilePane.add(labelFile, constraints)
        constraints.gridy--


        val nameTablePanel = JPanel()
        nameTablePanel.layout = BoxLayout(nameTablePanel, BoxLayout.X_AXIS)

        nameTablePanel.add(chooseFilePane)
        panelMain.add(tablePanel)
        panelMain.add(nameTablePanel)
    }

    private fun fileChooser(labelFile: JLabel) {
        val currentDirectory = File(directoryPath)

        val fileChooser = JFileChooser(currentDirectory)
        fileChooser.fileSelectionMode = JFileChooser.FILES_ONLY // Limit file selection to files only

        val result = fileChooser.showOpenDialog(null) // Show file selection dialog

        if (result == JFileChooser.APPROVE_OPTION) {
            selectedPath = fileChooser.selectedFile.path

            // Checking for the presence of an annotation @Entity
            if (fileChooser.selectedFile.toVirtualFile() != null) {
                val isEntity = isEntityFile(fileChooser.selectedFile.toVirtualFile()!!, project)
                if (isEntity) {
                    selectedFileName = fileChooser.selectedFile.nameWithoutExtension
                    // Getting the file package path
                    val sourceRoot = "src${File.separator}main${File.separator}java${File.separator}"
                    val packageName =
                        fileChooser.selectedFile.canonicalFile.parentFile.absolutePath.substringAfterLast(sourceRoot)
                    selectedFilePathPackage = packageName.replace(File.separator, ".") + ".$selectedFileName"

                    labelFile.text = "File Name: " + fileChooser.selectedFile.name

                    addBtnQuery.isEnabled = true
                    removeBtnQuery.isEnabled = true
                    checkConditions()
                } else {
                    Messages.showErrorDialog(
                        "The file is not a table!",
                        "Error:"
                    )
                }
            } else {
                return
            }
        }
    }

    private fun isEntityFile(file: VirtualFile, project: Project): Boolean {
        // Завантажуємо файл як KtFile через PSI
        val psiFile = PsiManager.getInstance(project).findFile(file) as? KtFile ?: return false

        // Обходимо всі декларації у файлі
        for (declaration in psiFile.declarations) {
            // Перевіряємо, чи є це клас або об'єкт
            if (declaration is KtClassOrObject) {
                // Отримуємо всі анотації, прив'язані до класу/об'єкта
                val annotations = declaration.annotationEntries
                for (annotation in annotations) {
                    val annotationName = annotation.shortName?.asString()
                    if (annotationName == "Entity") {
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun addDocumentListenerTextField(textField: JTextField) {
        // Document listener for a text field
        textField.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent) {
                checkConditions()
            }

            override fun removeUpdate(e: DocumentEvent) {
                checkConditions()
            }

            override fun changedUpdate(e: DocumentEvent) {
                checkConditions()
            }
        })
    }

    private fun checkConditions() {
        val nameTable = nameTableField.text
        // Condition check
        isOKActionEnabled = nameTable.isNotEmpty() && selectedFileName!!.isNotEmpty() && listQuery.model.size > 0
    }

    private fun setTextFieldToLatinCharactersOnly() {
        nameTableField.document = TextFieldRegex(nameTableField.document).setTextFieldToLatinCharactersOnly()
    }

    override fun createCenterPanel(): JComponent {
        return panelMain
    }

    fun getFileName(): String {
        return nameTableField.text.toString()
    }

    fun getPathChooseFile(): String {
        return selectedFilePathPackage.toString()
    }

    fun getNameChooseFile(): String {
        return selectedFileName.toString()
    }

    fun getListQuery(): List<Query> {
        return listModelQuery.elements().toList()
    }

    private fun Project.showNotification(message: String) {
        val groupId = "Database Plugin Notification Group"
        NotificationGroupManager.getInstance().getNotificationGroup(groupId)
            .createNotification("Database class", message, NotificationType.INFORMATION).notify(this)
    }
}