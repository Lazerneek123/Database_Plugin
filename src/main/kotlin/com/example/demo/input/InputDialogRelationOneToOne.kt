package com.example.demo.input

import com.example.demo.element.CapitalizeFirstLetter
import com.intellij.icons.AllIcons
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import com.intellij.util.IconUtil
import java.awt.BorderLayout
import java.io.File
import javax.swing.*


class InputDialogRelationOneToOne(private var directoryPath: String, private val packagePath: String) :
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


    init {
        title = "Relations 1:1"

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
            tableData1 = fileChooser(labelInfo1, listModelColumnTable1)
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
            tableData2 = fileChooser(labelInfo2, listModelColumnTable2)
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

    private fun fileChooser(label: JLabel, listModelColumnTable: DefaultListModel<String>): TableData {
        val currentDirectory = File(directoryPath)

        val fileChooser = JFileChooser(currentDirectory)
        fileChooser.fileSelectionMode = JFileChooser.FILES_ONLY // Limit file selection to files only

        val result = fileChooser.showOpenDialog(null) // Show file selection dialog
        val tableName: String
        val packagePath: String

        if (result == JFileChooser.APPROVE_OPTION) {
            val selectedFile = fileChooser.selectedFile // Get the selected file
            label.text = "Selected File: ${selectedFile.absolutePath}"

            val code = selectedFile.readText().trimIndent()
            val tableNamePattern = """tableName\s*=\s*"([^"]+)"""".toRegex()
            val columnNamePattern = """@ColumnInfo\(name\s*=\s*"([^"]+)"""".toRegex()

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

            label.text = "Table Name: $tableName; File Name: ${selectedFile.name} "

            listModelColumnTable.clear()
            val columnMatches = columnNamePattern.findAll(code)
            for (match in columnMatches) {
                val columnName = match.groupValues[1]
                listModelColumnTable.addElement(columnName)
            }
        } else {
            tableName = "Operation Cancelled"
            packagePath = ""
        }
        return TableData(tableName, packagePath)
    }

    fun getTableName1(): String {
        return tableData1.tableName
    }

    fun getTableName2(): String {
        return tableData2.tableName
    }

    fun getTablePackagePath1(): String {
        return tableData1.tablePackagePath
    }

    fun getTablePackagePath2(): String {
        return tableData2.tablePackagePath
    }

    fun getRelationName(): String {
        return CapitalizeFirstLetter().uppercaseChar(tableData1.tableName) + "And" +
                CapitalizeFirstLetter().uppercaseChar(tableData2.tableName)
    }

    fun getParentColumn(): String {
        return listColumnTable1.selectedValue
    }

    fun getEntityColumn(): String {
        return listColumnTable2.selectedValue
    }

    private data class TableData(val tableName: String, val tablePackagePath: String)

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