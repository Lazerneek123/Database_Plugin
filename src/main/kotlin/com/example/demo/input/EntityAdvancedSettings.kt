package com.example.demo.input

import com.example.demo.element.TextFieldRegex
import com.example.demo.model.Column
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import java.awt.BorderLayout
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.event.ListDataEvent
import javax.swing.event.ListDataListener

class EntityAdvancedSettings : DialogWrapper(true) {
    private val panel = JPanel()

    private val nameClassLabel = JLabel("Class Name:")
    private val nameClassField = JTextField()

    private val entityLabel = JLabel("Entity Attribute")

    private val listModelEntityAttribute = DefaultListModel<Any>()
    private val listEntityAttribute = JList(listModelEntityAttribute)

    private val listModelColumn = DefaultListModel<Column>()

    // Creating a list based on a model
    private val listColumn = JList(listModelColumn)
    private val columnLabel = JLabel("Columns")

    init {
        title = "Advanced Table Creation Settings"
        init()

        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

        setTextFieldToLatinCharactersOnly()

        val nameTablePanel = JPanel()
        nameTablePanel.layout = BoxLayout(nameTablePanel, BoxLayout.X_AXIS)

        nameTablePanel.add(nameClassLabel)
        nameTablePanel.add(nameClassField)

        addDocumentListenerTextField(nameClassField)
        panel.add(nameTablePanel)

        // Extra space between elements
        panel.add(Box.createVerticalStrut(10))

        val primaryKeyPanel = JPanel()
        primaryKeyPanel.layout = BoxLayout(primaryKeyPanel, BoxLayout.X_AXIS)
        primaryKeyPanel.add(entityLabel)
        panel.add(primaryKeyPanel)
        panel.add(Box.createVerticalStrut(5))

        panelCreateEntity()
        isOKActionEnabled = false
        panelCreateColumn()

        val advancedSettingsPanel = JPanel()
        advancedSettingsPanel.layout = BoxLayout(advancedSettingsPanel, BoxLayout.X_AXIS)
        panel.add(advancedSettingsPanel)
    }

    private fun panelCreateEntity() {
        panel.add(Box.createVerticalStrut(5))
        val panelPrimaryKey = JPanel()
        panelPrimaryKey.layout = BoxLayout(panelPrimaryKey, BoxLayout.X_AXIS)
        panelPrimaryKey.add(columnLabel)
        panel.add(panelPrimaryKey)
        panel.add(Box.createVerticalStrut(5))

        // Add button
        val addBtnPrimaryKey = JButton("Add")
        addBtnPrimaryKey.addActionListener {
            // Click your own InputDialog
            //val entityAdvancedSettings = EntityAttribute("", listModelPrimaryKey, listModelColumn)
            //entityAdvancedSettings.show()

            // Get the results when you click the OK button
            //if (entityAdvancedSettings.isOK) {
            //    val tableName = entityAdvancedSettings.getSelectedValue()
             //   listModelEntityAttribute.addElement(tableName)
            //}
        }

        // Delete button
        val removeBtnPrimaryKey = JButton("Delete")
        removeBtnPrimaryKey.addActionListener {
            val selectedIndex = listEntityAttribute.selectedIndex
            if (selectedIndex != -1) {
                listModelEntityAttribute.removeElementAt(selectedIndex)
            }
        }

        // Add a change listener to the list model
        listModelEntityAttribute.addListDataListener(object : ListDataListener {
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
        panel.add(JScrollPane(listEntityAttribute), BorderLayout.CENTER)

        // Adding buttons to a panel
        val buttonPanel = JPanel()
        buttonPanel.add(addBtnPrimaryKey)
        buttonPanel.add(removeBtnPrimaryKey)
        panel.add(buttonPanel, BorderLayout.SOUTH)
    }

    /*private fun panelCreatePrimaryKey() {
        panel.add(Box.createVerticalStrut(5))
        val panelPrimaryKey = JPanel()
        panelPrimaryKey.layout = BoxLayout(panelPrimaryKey, BoxLayout.X_AXIS)
        panelPrimaryKey.add(columnLabel)
        panel.add(panelPrimaryKey)
        panel.add(Box.createVerticalStrut(5))

        // Add button
        val addBtnPrimaryKey = JButton("Add")
        addBtnPrimaryKey.addActionListener {
            // Click your own InputDialog
            val primaryKey = PrimaryKey()
            primaryKey.show()

            // Get the results when you click the OK button
            if (primaryKey.isOK) {
                val columnName = primaryKey.getPrimaryKeyName()

                // Checking for a name match
                if (listModelEntityAttribute.elements().toList().any { it.name == columnName } ||
                    listModelColumn.elements().toList()
                        .any { it.name == columnName }
                ) {
                    Messages.showErrorDialog(
                        "This primary key name already exists. Change the name to something else!",
                        "Error:"
                    )
                } else {
                    val dataType = primaryKey.getPrimaryKeyDataType()
                    val value = primaryKey.getPrimaryKeyValue()
                    val autoGenerator = primaryKey.getPrimaryKeyAutoGenerate()

                    val newPrimaryKey = PrimaryKey(columnName, dataType, autoGenerator, value)
                    listModelEntityAttribute.addElement(newPrimaryKey)
                }
            }
        }

        // Delete button
        val removeBtnPrimaryKey = JButton("Delete")
        removeBtnPrimaryKey.addActionListener {
            val selectedIndex = listEntityAttribute.selectedIndex
            if (selectedIndex != -1) {
                listModelEntityAttribute.removeElementAt(selectedIndex)
            }
        }

        // Add a change listener to the list model
        listModelEntityAttribute.addListDataListener(object : ListDataListener {
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
        panel.add(JScrollPane(listEntityAttribute), BorderLayout.CENTER)

        // Adding buttons to a panel
        val buttonPanel = JPanel()
        buttonPanel.add(addBtnPrimaryKey)
        buttonPanel.add(removeBtnPrimaryKey)
        panel.add(buttonPanel, BorderLayout.SOUTH)
    }*/

    private fun panelCreateColumn() {
        panel.add(Box.createVerticalStrut(5))
        val panelColumn = JPanel()
        panelColumn.layout = BoxLayout(panelColumn, BoxLayout.X_AXIS)
        panelColumn.add(columnLabel)
        panel.add(panelColumn)
        panel.add(Box.createVerticalStrut(5))

        // Add button
        val addBtnColumn = JButton("Add")
        addBtnColumn.addActionListener {

            // Click your own InputDialog
            val column = Column()
            column.show()

            // Get the results when you click the OK button
            if (column.isOK) {
                val columnName = column.getColumnName()

                // Checking for a name match
                if (listModelColumn.elements().toList()
                        .any { it.name == columnName }
                ) {
                    Messages.showErrorDialog(
                        "This column name already exists. Change the name to something else!",
                        "Error:"
                    )
                } else {
                    val dataType = column.getColumnDataType()
                    val value = column.getColumnValue()
                    val nullable = column.getColumnNullable()

                    val newColumn = Column(columnName, dataType, value, nullable)
                    listModelColumn.addElement(newColumn)
                }
            }
        }

        // Delete button
        val removeBtnColumn = JButton("Delete")
        removeBtnColumn.addActionListener {
            val selectedIndex = listColumn.selectedIndex
            if (selectedIndex != -1) {
                listModelColumn.removeElementAt(selectedIndex)
            }
        }

        // Add a change listener to the list model
        listModelColumn.addListDataListener(object : ListDataListener {
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
        panel.add(JScrollPane(listColumn), BorderLayout.CENTER)

        // Adding buttons to a panel
        val buttonPanel = JPanel()
        buttonPanel.add(addBtnColumn)
        buttonPanel.add(removeBtnColumn)
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
        val nameTable = nameClassField.text

        // Condition check
        val conditionsMet: Boolean =
            nameTable.isNotEmpty() && listModelEntityAttribute.elements().toList().isNotEmpty() && listModelColumn.elements()
                .toList().isNotEmpty()

        // Activate or deactivate the OK button
        isOKActionEnabled = conditionsMet
    }

    private fun setTextFieldToLatinCharactersOnly() {
        nameClassField.document = TextFieldRegex(nameClassField.document).setTextFieldToLatinCharactersOnly()
    }

    override fun createCenterPanel(): JComponent {
        return panel
    }

    fun getTableName(): String {
        return nameClassField.text.toString()
    }

    /*fun getPrimaryKeysData(): List<PrimaryKey> {
        return listModelEntityAttribute.elements().toList()
    }*/

    fun getColumnsData(): List<Column> {
        return listModelColumn.elements().toList()
    }
}