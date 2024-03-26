package com.example.demo.input

import com.example.demo.model.PrimaryKey
import com.intellij.openapi.ui.DialogWrapper
import javax.swing.*

class InputDialogRelation : DialogWrapper(true) {
    private val panel = JPanel()

    private val nameLabel = JLabel("Table Name:")


    init {
        title = "Creation Of Tables And Connections Between Them"
        init()

        panelCreateTable()
        // Extra space between elements
        panel.add(Box.createVerticalStrut(10))
        panelCreateTable()
        // Extra space between elements
        panel.add(Box.createVerticalStrut(10))
        panelCreateRelationTable()
        isOKActionEnabled = true
    }

    private fun panelCreateTable() {
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

        val nameTablePanel = JPanel()
        nameTablePanel.layout = BoxLayout(nameTablePanel, BoxLayout.X_AXIS)

        nameTablePanel.add(nameLabel)
        panel.add(nameTablePanel)
    }

    private fun panelCreateRelationTable() {

    }

    fun getTableName1(): String {
        return "Table1"
    }

    fun getTableName2(): String {
        return "Table2"
    }

    fun getTableNameRelation(): String {
        return "TableRelation"
    }

    fun getPrimaryKey1(): PrimaryKey {
        return PrimaryKey("id1", "Int", false, "1")
    }

    fun getPrimaryKey2(): PrimaryKey {
        return PrimaryKey("id2", "Int", false, "2")
    }

    override fun createCenterPanel(): JComponent {
        return panel
    }
}