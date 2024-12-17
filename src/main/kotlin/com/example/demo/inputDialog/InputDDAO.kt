package com.example.demo.inputDialog

import com.example.demo.element.TextFieldRegex
import com.example.demo.model.*
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import com.intellij.util.IconUtil
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.File
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class InputDDAO(private val directoryPath: String, private val event: AnActionEvent) : DialogWrapper(true) {
    val message: String = "Create sucsasfull"
    private val panelMain = JPanel()
    private var selectedFileName: String? = null
    private var selectedFilePathPackage: String? = null

    private val nameLabel = JLabel("Name:")
    private val nameTableField = JTextField()

    private val listModelQuery = DefaultListModel<Query>()
    private val listQuery = JList(listModelQuery)

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
        val addBtnColumn = JButton("Add")
        addBtnColumn.addActionListener {
            val inputDialog = InputDQuery()
            inputDialog.show()

            // Get the results when you click the OK button
            if (inputDialog.isOK) {
                val name = inputDialog.getName()
                val queryType = inputDialog.getQueryType()
                val query = inputDialog.getManualInput()
                val valueQuery = inputDialog.getValueQuery()
                val onConflict = inputDialog.getOnConflict()

                listModelQuery.addElement(
                    Query(
                        queryType,
                        query,
                        name,
                        null,
                        onConflict,
                        valueQuery,
                        selectedFilePathPackage!!,
                        selectedFileName!!
                    )
                )
            }
        }

        // Delete button
        val removeBtnColumn = JButton("Delete")
        removeBtnColumn.addActionListener {
            val selectedIndex = listQuery.selectedIndex
            if (selectedIndex != -1) {
                listModelQuery.removeElementAt(selectedIndex)
            }
        }

        // Adding a list to a panel
        panel.add(JScrollPane(listQuery), BorderLayout.CENTER)

        // Adding buttons to a panel
        val buttonPanel = JPanel()
        buttonPanel.layout = BoxLayout(buttonPanel, BoxLayout.Y_AXIS)
        buttonPanel.add(addBtnColumn)
        buttonPanel.add(removeBtnColumn)
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

        val relationPanelOneToOne = JPanel(GridBagLayout())
        // Create a label for the image
        val labelIcon = JLabel(scaledIcon)
        val labelFile = JLabel("File Name:")
        labelIcon.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                fileChooser(labelFile)
            }
        })

        relationPanelOneToOne.add(labelIcon, constraints)
        constraints.gridy++
        relationPanelOneToOne.add(labelFile, constraints)
        constraints.gridy--


        val nameTablePanel = JPanel()
        nameTablePanel.layout = BoxLayout(nameTablePanel, BoxLayout.X_AXIS)

        nameTablePanel.add(relationPanelOneToOne)
        panelMain.add(tablePanel)
        panelMain.add(nameTablePanel)
    }

    private fun fileChooser(labelFile: JLabel) {
        val currentDirectory = File(directoryPath)

        val fileChooser = JFileChooser(currentDirectory)
        fileChooser.fileSelectionMode = JFileChooser.FILES_ONLY // Limit file selection to files only

        val result = fileChooser.showOpenDialog(null) // Show file selection dialog

        if (result == JFileChooser.APPROVE_OPTION) {
            val fileContent = fileChooser.selectedFile.readText()

            // Checking for the presence of an annotation @Entity
            val entityPattern = Regex("@Entity")
            val isEntity = entityPattern.containsMatchIn(fileContent)

            if (isEntity) {
                selectedFileName = fileChooser.selectedFile.nameWithoutExtension
                // Getting the file package path
                val sourceRoot = "src${File.separator}main${File.separator}java${File.separator}"
                val packageName =
                    fileChooser.selectedFile.canonicalFile.parentFile.absolutePath.substringAfterLast(sourceRoot)
                selectedFilePathPackage = packageName.replace(File.separator, ".") + ".$selectedFileName"

                labelFile.text = "File Name: " + fileChooser.selectedFile.name
                checkConditions()
            } else {
                Messages.showErrorDialog(
                    "The file does not contain the @Entity annotation!",
                    "Error:"
                )
            }
            checkConditions()
        }
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
        isOKActionEnabled = nameTable.isNotEmpty() && selectedFileName!!.isNotEmpty()
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
}