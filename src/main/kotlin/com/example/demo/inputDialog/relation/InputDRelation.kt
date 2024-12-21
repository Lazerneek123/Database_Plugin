package com.example.demo.inputDialog.relation

import com.example.demo.element.CapitalizeFirstLetter
import com.intellij.icons.AllIcons
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import com.intellij.util.IconUtil
import java.awt.BorderLayout
import java.io.File
import javax.swing.*

class InputDRelation(
    private var directoryPath: String,
    private val packagePath: String,
    relation: String
) :
    DialogWrapper(true) {
    private val panel = JPanel()

    // Model for list
    private val listModelColumnTable1 = DefaultListModel<String>()
    private val listColumnTable1 = JList(listModelColumnTable1)

    private val listModelColumnTable2 = DefaultListModel<String>()
    private val listColumnTable2 = JList(listModelColumnTable2)

    private val labelInfo1 = JLabel()
    private val labelInfo2 = JLabel()

    private lateinit var tableData1: TableData
    private lateinit var tableData2: TableData

    private lateinit var pathFile: String
    private lateinit var pathFile1: String
    private lateinit var pathFile2: String

    private var variables1: MutableList<Pair<String, String>> = mutableListOf()
    private var variables2: MutableList<Pair<String, String>> = mutableListOf()


    init {
        title = "Relations " + relation

        init()

        panelRelationTable()
        isOKActionEnabled =
            listModelColumnTable1.elements().toList().isNotEmpty() && listModelColumnTable2.elements().toList()
                .isNotEmpty()
    }

    private fun panelRelationTable() {
        panel.layout = BoxLayout(panel, BoxLayout.X_AXIS)

        val btnFileChooser1 = JButton("Choose File")
        btnFileChooser1.addActionListener {
            tableData1 = fileChooser(labelInfo1, listModelColumnTable1, variables1)
            pathFile1 = pathFile
            isOKActionEnabled =
                listModelColumnTable1.elements().toList().isNotEmpty() && listModelColumnTable2.elements().toList()
                    .isNotEmpty()
        }

        val relationTable1Panel = JPanel()
        relationTable1Panel.layout = BoxLayout(relationTable1Panel, BoxLayout.Y_AXIS)
        relationTable1Panel.add(labelInfo1)
        relationTable1Panel.add(Box.createVerticalStrut(5))
        relationTable1Panel.add(JScrollPane(listColumnTable1), BorderLayout.CENTER)
        relationTable1Panel.add(Box.createVerticalStrut(5))
        relationTable1Panel.add(btnFileChooser1, BorderLayout.CENTER)

        val btnFileChooser2 = JButton("Choose File")
        btnFileChooser2.addActionListener {
            tableData2 = fileChooser(labelInfo2, listModelColumnTable2, variables2)
            pathFile2 = pathFile
            isOKActionEnabled =
                listModelColumnTable1.elements().toList().isNotEmpty() && listModelColumnTable2.elements().toList()
                    .isNotEmpty()
        }

        val relationTable2Panel = JPanel()
        relationTable2Panel.layout = BoxLayout(relationTable2Panel, BoxLayout.Y_AXIS)
        relationTable2Panel.add(labelInfo2)
        relationTable2Panel.add(Box.createVerticalStrut(5))
        relationTable2Panel.add(JScrollPane(listColumnTable2), BorderLayout.CENTER)
        relationTable2Panel.add(Box.createVerticalStrut(5))
        relationTable2Panel.add(btnFileChooser2, BorderLayout.CENTER)

        panel.add(relationTable1Panel)
        val icon = AllIcons.General.ArrowSplitCenterH
        val scaledIcon = IconUtil.scale(icon, 5.0)
        val labelRelationIcon = JLabel(scaledIcon)
        panel.add(labelRelationIcon)
        panel.add(relationTable2Panel)
    }

    private fun fileChooser(
        label: JLabel,
        listModelColumnTable: DefaultListModel<String>,
        variables: MutableList<Pair<String, String>>
    ): TableData {
        val currentDirectory = File(directoryPath)

        val fileChooser = JFileChooser(currentDirectory)
        fileChooser.fileSelectionMode = JFileChooser.FILES_ONLY // Limit file selection to files only

        val result = fileChooser.showOpenDialog(null) // Show file selection dialog

        val tableName: String
        val packagePath: String
        val className: String

        if (result == JFileChooser.APPROVE_OPTION) {
            val selectedFile = fileChooser.selectedFile // Get the selected file
            pathFile = selectedFile.path
            //label.text = "Selected File: ${selectedFile.absolutePath}"


            val variablePattern = """var (\w+): (\w+)""".toRegex()
            val matches = variablePattern.findAll(selectedFile.readText())
            matches.forEach { match ->
                val variableName = match.groupValues[1]
                val variableType = match.groupValues[2]
                variables.add(variableName to variableType)
            }


            val code = selectedFile.readText().trimIndent()
            val tableNamePattern = """tableName\s*=\s*"([^"]+)"""".toRegex()
            val columnNamePattern = """@ColumnInfo\(name\s*=\s*"([^"]+)"""".toRegex()
            val foreignKeyPattern = """import androidx.room.ForeignKey""".toRegex()

            // Check if the file already contains a ForeignKey relation
            val foreignKeyMatch = foreignKeyPattern.find(code)
            if (foreignKeyMatch != null) {
                JOptionPane.showMessageDialog(
                    null,
                    "The selected file already has a relation!",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE
                )
                tableName = "Operation Cancelled"
                packagePath = ""
                className = ""
                label.text = "Operation Cancelled"
                listModelColumnTable.clear()

            } else {
                val tableNameMatch = tableNamePattern.find(code)
                tableName = tableNameMatch?.groupValues?.get(1) ?: "Unknown"

                // Getting the file package path
                val sourceRoot = "src${File.separator}main${File.separator}java${File.separator}"
                val packagePathWithoutSourceRoot =
                    selectedFile.canonicalFile.parentFile.absolutePath.substringAfterLast(sourceRoot)

                // Path compatibility check
                packagePath = if (packagePathWithoutSourceRoot.replace(File.separator, ".") == this.packagePath) {
                    ""
                } else {
                    packagePathWithoutSourceRoot.replace(File.separator, ".") + ".${
                        CapitalizeFirstLetter().uppercaseChar(tableName)
                    }"
                }

                className = getClassNameFromFile(selectedFile)!!

                label.text = "Table Name: $tableName; File Name: ${selectedFile.name} "

                listModelColumnTable.clear()
                val columnMatches = columnNamePattern.findAll(code)
                for (match in columnMatches) {
                    val columnName = match.groupValues[1]
                    listModelColumnTable.addElement(columnName)
                }
            }

        } else {
            tableName = "Operation Cancelled"
            packagePath = ""
            className = ""
        }
        return TableData(tableName, packagePath, className)
    }

    private fun getClassNameFromFile(file: File): String? {
        val fileContent = file.readText()

        // We use a regular expression to find the class declaration
        val classPattern = """class\s+([A-Za-z_]\w*)""".toRegex()
        val matchResult = classPattern.find(fileContent)

        return matchResult?.groupValues?.get(1)
    }

    fun getTableName1(): String {
        return tableData1.tableName
    }

    fun getTableName2(): String {
        return tableData2.tableName
    }

    fun getCrossRefName(): String {
        return CapitalizeFirstLetter().uppercaseChar(tableData1.tableName) + CapitalizeFirstLetter().uppercaseChar(
            tableData2.tableName
        ) + "CrossRef"
    }

    fun getTablePackagePath1(): String {
        return tableData1.tablePackagePath
    }

    fun getTablePackagePath2(): String {
        return tableData2.tablePackagePath
    }

    fun getRelationName(): String {
        return CapitalizeFirstLetter().uppercaseChar(tableData1.tableName) + "With" +
                CapitalizeFirstLetter().uppercaseChar(tableData2.tableName)
    }

    fun getRelationManyToManyName(): String {
        return CapitalizeFirstLetter().uppercaseChar(tableData1.tableName) + "sWith" +
                CapitalizeFirstLetter().uppercaseChar(tableData2.tableName) + "s"
    }

    fun getParentColumn(): String {
        return listColumnTable1.selectedValue
    }

    fun getEntityColumn(): String {
        return listColumnTable2.selectedValue
    }

    fun getPathFile1(): String {
        return pathFile1
    }

    fun getPathFile2(): String {
        return pathFile2
    }

    fun getClassName1(): String {
        return tableData1.className
    }

    fun getClassName2(): String {
        return tableData2.className
    }

    fun getVariables1(): MutableList<Pair<String, String>> {
        return variables1
    }

    fun getVariables2(): MutableList<Pair<String, String>> {
        return variables2
    }

    private data class TableData(val tableName: String, val tablePackagePath: String, val className: String)

    override fun createCenterPanel(): JComponent {
        return panel
    }

    override fun doOKAction() {
        if (!listColumnTable1.isSelectionEmpty && !listColumnTable2.isSelectionEmpty) {
            super.doOKAction()
        } else {
            Messages.showErrorDialog(
                "Please select the parent column in the first list and the entity column in the second!",
                "Error:"
            )
        }
    }
}