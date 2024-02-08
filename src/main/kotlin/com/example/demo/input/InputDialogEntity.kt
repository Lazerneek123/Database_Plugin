package com.example.demo.input

import com.example.demo.model.ColumnData
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import java.awt.BorderLayout
import java.awt.event.ItemEvent
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.event.ListDataEvent
import javax.swing.event.ListDataListener
import javax.swing.text.AttributeSet
import javax.swing.text.PlainDocument

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

        setTextFieldToLatinCharactersOnly()

        val nameTablePanel = JPanel()
        nameTablePanel.layout = BoxLayout(nameTablePanel, BoxLayout.X_AXIS)

        nameTablePanel.add(nameLabel)
        nameTablePanel.add(nameTableField)

        addDocumentListenerTextField(nameTableField)
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
            checkConditions()
        }
        primaryKeyInputPanel.add(primaryKeyAutoGenerate)

        addDocumentListenerTextField(primaryKeyTextFieldName)
        addDocumentListenerTextField(primaryKeyTextFieldValue)
        panel.add(primaryKeyInputPanel)

        isOKActionEnabled = false
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

            // Click your own InputDialog
            val inputDialogColumn = InputDialogColumn()
            inputDialogColumn.show()

            // Get the results when you click the OK button
            if (inputDialogColumn.isOK) {
                val columnName = inputDialogColumn.getColumnName()

                // Checking for a name match
                if (listModel.elements().toList()
                        .any { it.name == columnName } && primaryKeyTextFieldName.text == columnName
                ) {
                    Messages.showErrorDialog(
                        "This column name already exists. Change the name to something else!",
                        "Error:"
                    )
                } else {
                    val columnDataType = inputDialogColumn.getColumnDataType()
                    val columnValue = inputDialogColumn.getColumnValue()

                    val newColumn = ColumnData(columnName, columnDataType, columnValue)
                    listModel.addElement(newColumn)
                }
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

        // Add a change listener to the list model
        listModel.addListDataListener(object : ListDataListener {
            override fun intervalAdded(e: ListDataEvent) {
                checkConditions()
            }

            override fun intervalRemoved(e: ListDataEvent) {
                checkConditions()
            }

            override fun contentsChanged(e: ListDataEvent) {
                checkConditions()
            }
        })

        // Adding a list to a panel
        panel.add(JScrollPane(list), BorderLayout.CENTER)

        // Adding buttons to a panel
        val buttonPanel = JPanel()
        buttonPanel.add(addButton)
        buttonPanel.add(removeButton)
        panel.add(buttonPanel, BorderLayout.SOUTH)
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
        val namePrimaryKey = primaryKeyTextFieldName.text

        // Condition check
        val conditionsMet: Boolean = if (primaryKeyAutoGenerate.isSelected) {
            nameTable.isNotEmpty() && namePrimaryKey.isNotEmpty() && listModel.elements().toList().isNotEmpty()
        } else {
            nameTable.isNotEmpty() && namePrimaryKey.isNotEmpty() && listModel.elements().toList()
                .isNotEmpty() && primaryKeyTextFieldValue.text.isNotEmpty()
        }

        // Activate or deactivate the OK button
        isOKActionEnabled = conditionsMet
    }

    private fun setTextFieldToLatinCharactersOnly() {
        nameTableField.document = createDocumentForRegex("[a-zA-Z]*")
        primaryKeyTextFieldName.document = createDocumentForRegex("[a-zA-Z]*")
    }

    private fun createDocumentForRegex(regex: String): PlainDocument {
        return object : PlainDocument() {
            override fun insertString(offset: Int, str: String?, attr: AttributeSet?) {
                if (str == null) return
                if (str.matches(Regex(regex))) {
                    super.insertString(offset, str, attr)
                }
            }
        }
    }

    override fun createCenterPanel(): JComponent {
        return panel
    }

    fun getTableName(): String {
        return nameTableField.text.toString()
    }

    fun getPrimaryKeyName(): String {
        return primaryKeyTextFieldName.text.toString()
    }

    fun getPrimaryKeyAutoGenerate(): Boolean {
        return isActionEnabled
    }

    fun getPrimaryKeyValue(): String {
        return primaryKeyTextFieldValue.text.toString()
    }

    fun getColumnsData(): List<ColumnData> {
        return listModel.elements().toList()
    }
}
