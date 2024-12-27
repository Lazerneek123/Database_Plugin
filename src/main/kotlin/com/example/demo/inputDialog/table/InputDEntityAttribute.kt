package com.example.demo.inputDialog.table

import com.example.demo.model.Column
import com.example.demo.model.PrimaryKey
import com.example.demo.tableConfig.EntityAttribute
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import java.awt.BorderLayout
import java.awt.CardLayout
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.event.ListDataEvent
import javax.swing.event.ListDataListener

class InputDEntityAttribute(
    selectedElement: String,
    listModelPrimaryKey: DefaultListModel<PrimaryKey>,
    listModelColumn: DefaultListModel<Column>,
    directoryPath: String,
    project: Project
) : DialogWrapper(true) {
    private val panel = JPanel()

    private val panelMain = JPanel(CardLayout())

    private val attributeEntity = EntityAttribute().get()

    private val initialValues = DefaultListModel<Map.Entry<String?, Any?>?>()

    private var currentCardName: String = selectedElement

    // Panel for JTextField (TableName)
    private val tableNameField = JTextField(20)
    private val tableNamePanel = JPanel().apply {
        add(JLabel("Value:"))
        tableNameField.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent) {
                checkConditionsTableNamePanel()
            }

            override fun removeUpdate(e: DocumentEvent) {
                checkConditionsTableNamePanel()
            }

            override fun changedUpdate(e: DocumentEvent) {
                checkConditionsTableNamePanel()
            }
        })
        add(tableNameField)
        isOKActionEnabled = false
    }

    private fun checkConditionsTableNamePanel() {
        val conditionsMet: Boolean = tableNameField.text.isNotEmpty()
        isOKActionEnabled = conditionsMet
    }

    // Панель для JList (PrimaryKeys)
    private val primaryKeyListModel = DefaultListModel<String>()
    private val primaryKeyList = JList(primaryKeyListModel)
    private val primaryKeyPanel = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)

        // Add a list of all rows
        listModelPrimaryKey.elements().toList().forEach { element ->
            primaryKeyListModel.addElement(element.name)
        }
        listModelColumn.elements().toList().forEach { element ->
            primaryKeyListModel.addElement(element.name)
        }

        primaryKeyList.addListSelectionListener {
            isOKActionEnabled = primaryKeyListModel.size() > 0
        }

        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.X_AXIS)
        add(JScrollPane(primaryKeyList))
        add(panel)
    }

    // Панель для ForeignKeys
    private val foreignKeyListModel = DefaultListModel<com.example.demo.model.ForeignKey>()
    private val foreignKeyList = JList(foreignKeyListModel)
    private val foreignKeysPanel = JPanel().apply {
        val addBtnForeignKeys = JButton("Add")
        addBtnForeignKeys.addActionListener {
            val inputDialogIndex = InputDForeignKey(listModelPrimaryKey, listModelColumn, directoryPath, project)
            inputDialogIndex.show()

            // Get the results when you click the OK button
            if (inputDialogIndex.isOK) {
                val foreignKeyElement = inputDialogIndex.getForeignKey()

                foreignKeyListModel.addElement(foreignKeyElement)
                if (!foreignKeyListModel.isEmpty) {
                    isOKActionEnabled = true
                }
            }
        }

        val removeBtnForeignKeys = JButton("Delete")
        removeBtnForeignKeys.addActionListener {
            val selectedIndex = foreignKeyList.selectedIndex
            if (selectedIndex != -1) {
                foreignKeyListModel.removeElementAt(selectedIndex)
                if (foreignKeyListModel.isEmpty) {
                    isOKActionEnabled = false
                }
            }
        }

        // Adding a list to a panel
        add(JScrollPane(foreignKeyList), BorderLayout.CENTER)

        // Adding buttons to a panel
        val buttonPanel = JPanel()
        buttonPanel.add(addBtnForeignKeys)
        buttonPanel.add(removeBtnForeignKeys)
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        add(buttonPanel, BorderLayout.SOUTH)
    }

    private fun checkConditions() {
        // Condition check
        val conditionsMet: Boolean = false

        // Activate or deactivate the OK button
        isOKActionEnabled = conditionsMet
    }

    // Панель для Indices
    private val indexListModel = DefaultListModel<com.example.demo.model.Index>()
    private val indexList = JList(indexListModel)
    private val indicesPanel = JPanel().apply {
        // Add button
        val addBtnIndex = JButton("Add")
        addBtnIndex.addActionListener {
            val inputDialogInputDIndex = InputDIndex(listModelPrimaryKey, listModelColumn)
            inputDialogInputDIndex.show()

            // Get the results when you click the OK button
            if (inputDialogInputDIndex.isOK) {
                val indexElement = inputDialogInputDIndex.getIndex()
                indexListModel.addElement(indexElement)
                if (!indexListModel.isEmpty) {
                    isOKActionEnabled = true
                }
            }
        }

        // Delete button
        val removeBtnIndex = JButton("Delete")
        removeBtnIndex.addActionListener {
            val selectedIndex = indexList.selectedIndex
            if (selectedIndex != -1) {
                indexListModel.removeElementAt(selectedIndex)
                if (indexListModel.isEmpty) {
                    isOKActionEnabled = false
                }
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
        add(JScrollPane(indexList), BorderLayout.CENTER)

        // Adding buttons to a panel
        val buttonPanel = JPanel()
        buttonPanel.add(addBtnIndex)
        buttonPanel.add(removeBtnIndex)
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        add(buttonPanel, BorderLayout.SOUTH)
    }

    // Панель для InheritSuperIndices
    private val comboBoxInheritSuperIndices = JComboBox(arrayOf("true", "false"))
    private val inheritSuperIndicesPanel = JPanel().apply {
        comboBoxInheritSuperIndices.addActionListener {
            isOKActionEnabled = comboBoxInheritSuperIndices.selectedItem != null
        }
        add(comboBoxInheritSuperIndices)
    }

    // Панель для IgnoredColumns
    private val ignoredColumnsListModel = DefaultListModel<String>()
    private val ignoredColumnsList = JList(ignoredColumnsListModel)
    private val ignoredColumnsPanel = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)

        // Add a list of all rows
        listModelPrimaryKey.elements().toList().forEach { element ->
            ignoredColumnsListModel.addElement(element.name)
        }
        listModelColumn.elements().toList().forEach { element ->
            ignoredColumnsListModel.addElement(element.name)
        }

        ignoredColumnsList.addListSelectionListener {
            isOKActionEnabled = ignoredColumnsListModel.size() > 0
        }

        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.X_AXIS)
        add(JScrollPane(ignoredColumnsList))
        add(panel)
    }

    init {
        title = currentCardName
        init()
        isOKActionEnabled = false

        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

        initialValues.addElement(mapOf(attributeEntity[0] to currentCardName).entries.first())
        initialValues.addElement(mapOf(attributeEntity[1] to null).entries.first())
        initialValues.addElement(mapOf(attributeEntity[2] to null).entries.first())
        initialValues.addElement(mapOf(attributeEntity[3] to null).entries.first())
        initialValues.addElement(mapOf(attributeEntity[4] to null).entries.first())
        initialValues.addElement(mapOf(attributeEntity[5] to null).entries.first())

        val nameColumnPanel = JPanel()
        nameColumnPanel.layout = BoxLayout(nameColumnPanel, BoxLayout.X_AXIS)

        panel.add(nameColumnPanel)

        val comboBoxPanel = JPanel()
        comboBoxPanel.layout = BoxLayout(comboBoxPanel, BoxLayout.X_AXIS)
        panelMain.add(comboBoxPanel)

        val valuePanel = JPanel()
        valuePanel.layout = BoxLayout(valuePanel, BoxLayout.X_AXIS)


        panelMain.add(tableNamePanel, attributeEntity[0])
        panelMain.add(primaryKeyPanel, attributeEntity[1])
        panelMain.add(foreignKeysPanel, attributeEntity[2])
        panelMain.add(indicesPanel, attributeEntity[3])
        panelMain.add(inheritSuperIndicesPanel, attributeEntity[4])
        panelMain.add(ignoredColumnsPanel, attributeEntity[5])


        val cl = panelMain.layout as CardLayout
        cl.show(panelMain, currentCardName)


        // Основна панель
        val mainPanel = JPanel()
        mainPanel.layout = BoxLayout(mainPanel, BoxLayout.Y_AXIS)
        mainPanel.add(panelMain)

        panel.add(mainPanel)

        panel.add(valuePanel)
    }
    // A function to check if the values in the model have changed
    fun getInitialValues(): DefaultListModel<Map.Entry<String?, Any?>?> {
        return initialValues
    }

    override fun createCenterPanel(): JComponent {
        return panel
    }

    fun getSelectedValue(): Map.Entry<String, Any?> {
        return when (currentCardName) {
            attributeEntity[0] -> mapOf(currentCardName to tableNameField.text).entries.first()
            attributeEntity[1] -> mapOf(currentCardName to primaryKeyList.selectedValuesList).entries.first()
            attributeEntity[2] -> mapOf(currentCardName to foreignKeyListModel.elements().toList()).entries.first()
            attributeEntity[3] -> mapOf(currentCardName to indexListModel.elements().toList()).entries.first()
            attributeEntity[4] -> mapOf(currentCardName to comboBoxInheritSuperIndices.selectedItem!!).entries.first()
            attributeEntity[5] -> mapOf(currentCardName to ignoredColumnsList.selectedValuesList).entries.first()
            else -> mapOf("key" to "value").entries.first()
        }
    }
}
