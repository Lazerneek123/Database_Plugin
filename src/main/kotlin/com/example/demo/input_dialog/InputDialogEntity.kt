package com.example.demo.input_dialog

import com.example.demo.model.ColumnData
import com.intellij.openapi.ui.DialogWrapper
import java.awt.BorderLayout
import java.awt.event.ItemEvent
import javax.swing.*

class InputDialogEntity : DialogWrapper(true) {
    private val panel = JPanel()

    private val nameLabel = JLabel("Table Name:")
    private val nameTableField = JTextField()

    private val primaryKeyLabel = JLabel("Primary Key")
    private val primaryKeyLabelName = JLabel("Name:")
    private val primaryKeyTextFieldName = JTextField()
    private val primaryKeyLabelValue = JLabel("Value:")
    private val primaryKeyTextFieldValue = JTextField()

    private val primaryKeyAutoGenerate = JCheckBox("Auto Generate")
    private var isActionEnabled: Boolean = true

    // Model for list
    private val listModel = DefaultListModel<ColumnData>()

    // Creating a list based on a model
    private val list = JList(listModel)
    private val columnLabel = JLabel("Columns")

    init {
        title = "Create Table"
        init()

        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

        val nameTablePanel = JPanel()
        nameTablePanel.layout = BoxLayout(nameTablePanel, BoxLayout.X_AXIS)

        nameTablePanel.add(nameLabel)
        nameTablePanel.add(nameTableField)
        panel.add(nameTablePanel)

        // Extra space between elements
        panel.add(Box.createVerticalStrut(10))

        val primaryKeyPanel = JPanel()
        primaryKeyPanel.layout = BoxLayout(primaryKeyPanel, BoxLayout.X_AXIS)
        primaryKeyPanel.add(primaryKeyLabel)
        panel.add(primaryKeyPanel)
        panel.add(Box.createVerticalStrut(5))

        val primaryKeyInputPanel = JPanel()
        primaryKeyInputPanel.layout = BoxLayout(primaryKeyInputPanel, BoxLayout.X_AXIS)
        primaryKeyInputPanel.add(primaryKeyLabelName)
        primaryKeyInputPanel.add(primaryKeyTextFieldName)
        primaryKeyInputPanel.add(primaryKeyLabelValue)
        primaryKeyInputPanel.add(primaryKeyTextFieldValue)

        primaryKeyAutoGenerate.isSelected = true
        primaryKeyTextFieldValue.isEnabled = false
        // We add an event listener for the checkBox
        primaryKeyAutoGenerate.addItemListener { e ->
            if (e.stateChange == ItemEvent.SELECTED) {
                // If the checkBox is checked, set isActionEnabled to true
                isActionEnabled = true
                primaryKeyTextFieldValue.isEnabled = false
            } else if (e.stateChange == ItemEvent.DESELECTED) {
                // If the checkBox is not checked, set isActionEnabled to false
                isActionEnabled = false
                primaryKeyTextFieldValue.isEnabled = true
            }
        }
        primaryKeyInputPanel.add(primaryKeyAutoGenerate)

        panel.add(primaryKeyInputPanel)

        panelCreateColumn()
    }

    private fun panelCreateColumn() {
        panel.add(Box.createVerticalStrut(5))
        val panelColumn = JPanel()
        panelColumn.layout = BoxLayout(panelColumn, BoxLayout.X_AXIS)
        panelColumn.add(columnLabel)
        panel.add(panelColumn)
        panel.add(Box.createVerticalStrut(5))

        // Add button
        val addButton = JButton("Add")
        addButton.addActionListener {

            // Виклик вашого власного InputDialog
            val inputDialogColumn = InputDialogColumn()
            inputDialogColumn.show()

            // Отримайте результати, якщо потрібно
            if (inputDialogColumn.isOK) {
                val columnName = inputDialogColumn.getColumnName()
                val columnDataType = inputDialogColumn.getColumnDataType()
                val columnValue = inputDialogColumn.getColumnValue()

                val newColumn = ColumnData(columnName, columnDataType, columnValue)
                listModel.addElement(newColumn)
            }

        }

        // Delete button
        val removeButton = JButton("Delete")
        removeButton.addActionListener {
            val selectedIndex = list.selectedIndex
            if (selectedIndex != -1) {
                listModel.removeElementAt(selectedIndex)
            }
        }

        // Adding a list to a panel
        panel.add(JScrollPane(list), BorderLayout.CENTER)

        // Adding buttons to a panel
        val buttonPanel = JPanel()
        buttonPanel.add(addButton)
        buttonPanel.add(removeButton)
        panel.add(buttonPanel, BorderLayout.SOUTH)
    }

    override fun createCenterPanel(): JComponent {
        return panel
    }

    fun getTableName(): String {
        return nameTableField.text
    }

    fun getPrimaryKeyName(): String {
        return primaryKeyTextFieldName.text
    }

    fun getPrimaryKeyAutoGenerate(): Boolean {
        return isActionEnabled
    }

    fun getPrimaryKeyValue(): String {
        return primaryKeyTextFieldValue.text
    }

    fun getColumnsData(): List<ColumnData> {
        val listColumn = ArrayList<ColumnData>()
        for (i in 0 until listModel.size) {
            listColumn.add(listModel.getElementAt(i))
        }
        return listColumn
    }
}
