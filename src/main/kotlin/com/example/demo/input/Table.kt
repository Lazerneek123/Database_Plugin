package com.example.demo.input

import com.example.demo.element.TextFieldRegex
import com.example.demo.model.Column
import com.example.demo.model.PrimaryKey
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import java.awt.BorderLayout
import java.awt.CardLayout
import java.awt.event.ActionEvent
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.event.ListDataEvent
import javax.swing.event.ListDataListener

class Table : DialogWrapper(true) {
    private val cardLayout = CardLayout()
    private val panelMain = JPanel(cardLayout)

    private val nameLabel = JLabel("Name:")
    private val nameTableField = JTextField()

    private val primaryKeyLabel = JLabel("Primary Key")
    private val columnLabel = JLabel("Columns")

    private val attribute = com.example.demo.tableConfig.EntityAttribute().get()

    // Model for list
    private val listModelPrimaryKey = DefaultListModel<PrimaryKey>()

    // Creating a list based on a model
    private val listPrimaryKey = JList(listModelPrimaryKey)

    private val listModelColumn = DefaultListModel<Column>()
    private val listColumn = JList(listModelColumn)

    init {
        title = "Create Table"
        init()

        // Додаємо панелі для кроків
        panelMain.add(createStep1Panel(), "Step 1")
        panelMain.add(createStep2Panel(), "Step 2")


    }

    private fun createStep2Panel(): JPanel {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

        val listModelEntityAttribute = DefaultListModel<Map.Entry<String, Any>>()
        val listEntityAttribute = JList(listModelEntityAttribute)

        val nameLabel = JLabel("Entity")
        val panelLabel = JPanel()
        panelLabel.layout = BoxLayout(panelLabel, BoxLayout.X_AXIS)
        panelLabel.add(nameLabel)
        panel.add(panelLabel, BorderLayout.CENTER)


        var selectedElement = ""

        attribute.forEach { attributeName -> listModelEntityAttribute.addElement(mapOf(attributeName to "").entries.first()) }

        panel.add(Box.createVerticalStrut(5))
        val panelPrimaryKey = JPanel()
        panelPrimaryKey.layout = BoxLayout(panelPrimaryKey, BoxLayout.Y_AXIS)
        panel.add(panelPrimaryKey)
        panel.add(Box.createVerticalStrut(5))

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
        listEntityAttribute.addListSelectionListener { event ->
            if (!event.valueIsAdjusting) {
                val element = listEntityAttribute.selectedValue
                if (element != null) {
                    selectedElement = element.key
                    // Click your own InputDialog
                    val entityAdvancedSettings = EntityAttribute(selectedElement, listModelPrimaryKey, listModelColumn)
                    entityAdvancedSettings.show()

                    // Get the results when you click the OK button
                    if (entityAdvancedSettings.isOK) {
                        val pair = entityAdvancedSettings.getSelectedValue()
                        listModelEntityAttribute.elements().toList().forEachIndexed { index, element ->
                            if (element.key == pair.key) {
                                val updatedElement = mapOf(element.key to pair.value).entries.first()
                                listModelEntityAttribute.set(index, updatedElement)
                            }
                        }
                    }
                }
            }
        }

        // Adding a list to a panel
        panel.add(JScrollPane(listEntityAttribute), BorderLayout.CENTER)

        // Adding buttons to a panel
        val buttonPanel = JPanel()
        panel.add(buttonPanel, BorderLayout.SOUTH)

        return panel
    }

    private fun createStep1Panel(): JPanel {
        val panel = JPanel()

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

        panelCreatePrimaryKey(panel)
        //isOKActionEnabled = false
        panelCreateColumn(panel)

        return panel
    }

    private fun panelCreateColumn(panel: JPanel) {
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
                        .any { it.name == columnName } ||
                    listModelPrimaryKey.elements().toList()
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

    private fun panelCreatePrimaryKey(panel: JPanel) {
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
                if (listModelPrimaryKey.elements().toList().any { it.name == columnName } ||
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
                    listModelPrimaryKey.addElement(newPrimaryKey)
                }
            }
        }

        // Delete button
        val removeBtnPrimaryKey = JButton("Delete")
        removeBtnPrimaryKey.addActionListener {
            val selectedIndex = listPrimaryKey.selectedIndex
            if (selectedIndex != -1) {
                listModelPrimaryKey.removeElementAt(selectedIndex)
            }
        }

        // Add a change listener to the list model
        listModelPrimaryKey.addListDataListener(object : ListDataListener {
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
        panel.add(JScrollPane(listPrimaryKey), BorderLayout.CENTER)

        // Adding buttons to a panel
        val buttonPanel = JPanel()
        buttonPanel.add(addBtnPrimaryKey)
        buttonPanel.add(removeBtnPrimaryKey)
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

        // Condition check
        val conditionsMet: Boolean =
            nameTable.isNotEmpty() && listModelPrimaryKey.elements().toList().isNotEmpty() && listModelColumn.elements()
                .toList().isNotEmpty()

        // Activate or deactivate the OK button
        isOKActionEnabled = conditionsMet
    }

    private fun setTextFieldToLatinCharactersOnly() {
        nameTableField.document = TextFieldRegex(nameTableField.document).setTextFieldToLatinCharactersOnly()
    }

    override fun createCenterPanel(): JComponent {
        return panelMain
    }

    fun getTableName(): String {
        return nameTableField.text.toString()
    }

    fun getPrimaryKeysData(): List<PrimaryKey> {
        return listModelPrimaryKey.elements().toList()
    }

    fun getColumnsData(): List<Column> {
        return listModelColumn.elements().toList()
    }

    override fun createActions(): Array<Action> {
        return arrayOf(
            object : DialogWrapperAction("Previous") {
                override fun doAction(e: ActionEvent?) {
                    cardLayout.previous(panelMain)
                }
            },
            object : DialogWrapperAction("Next") {
                override fun doAction(e: ActionEvent?) {
                    cardLayout.next(panelMain)
                }
            },
            cancelAction,
            object : DialogWrapperAction("Finish") {
                override fun doAction(e: ActionEvent?) {
                    close(OK_EXIT_CODE)
                }
            }
        )
    }
}