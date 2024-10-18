package com.example.demo.input

import com.example.demo.model.Column
import com.example.demo.model.PrimaryKey
import com.example.demo.tableConfig.EntityAttribute
import com.example.demo.tableConfig.ForeignKeyAttribute
import com.intellij.openapi.ui.DialogWrapper
import java.awt.BorderLayout
import java.awt.CardLayout
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.event.ListDataEvent
import javax.swing.event.ListDataListener

class EntityAttribute(
    selectedElement: String,
    listModelPrimaryKey: DefaultListModel<PrimaryKey>,
    listModelColumn: DefaultListModel<Column>
) : DialogWrapper(true) {
    private val panel = JPanel()

    private val panelMain = JPanel(CardLayout())

    private val attributeEntity = EntityAttribute().get()
    private val attributeForeignKey = ForeignKeyAttribute().get()

    private var currentCardName: String = selectedElement

    // Панель для JTextField (TableName)
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
    private val foreignKeyListModel = DefaultListModel<Map.Entry<String, Any>>()
    private val foreignKeyList = JList(foreignKeyListModel)
    private val foreignKeysPanel = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)

        val nameLabel = JLabel("Foreign Key")
        val panelLabel = JPanel()
        panelLabel.layout = BoxLayout(panelLabel, BoxLayout.X_AXIS)
        panelLabel.add(nameLabel)
        add(panelLabel, BorderLayout.CENTER)


        var selectedElement = ""

        attributeForeignKey.forEach { attributeName -> foreignKeyListModel.addElement(mapOf(attributeName to "").entries.first()) }

        add(Box.createVerticalStrut(5))
        val panelPrimaryKey = JPanel()
        panelPrimaryKey.layout = BoxLayout(panelPrimaryKey, BoxLayout.Y_AXIS)
        add(panelPrimaryKey)
        add(Box.createVerticalStrut(5))

        // Add a change listener to the list model
        foreignKeyListModel.addListDataListener(object : ListDataListener {
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
        foreignKeyList.addListSelectionListener { event ->
            if (!event.valueIsAdjusting) {
                val element = foreignKeyList.selectedValue
                if (element != null) {
                    selectedElement = element.key
                    // Click your own InputDialog
                    val entityAdvancedSettings = EntityAttribute(selectedElement, listModelPrimaryKey, listModelColumn)
                    entityAdvancedSettings.show()

                    // Get the results when you click the OK button
                    if (entityAdvancedSettings.isOK) {
                        val pair = entityAdvancedSettings.getSelectedValue()
                        foreignKeyListModel.elements().toList().forEachIndexed { index, element ->
                            if (element.key == pair.key) {
                                val updatedElement = mapOf(element.key to pair.value).entries.first()
                                foreignKeyListModel.set(index, updatedElement)
                            }
                        }
                    }
                }
            }
        }

        add(JScrollPane(foreignKeyList), BorderLayout.CENTER)
    }

    private fun checkConditions() {
        // Condition check
        val conditionsMet: Boolean = false

        // Activate or deactivate the OK button
        isOKActionEnabled = conditionsMet
    }

    // Панель для Indices
    private val indicesPanel = JPanel().apply {
        add(JLabel("Indices Configuration"))
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

        val nameColumnPanel = JPanel()
        nameColumnPanel.layout = BoxLayout(nameColumnPanel, BoxLayout.X_AXIS)

        //checkTextFieldToLatinCharactersOnly()
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

        //updateValueField()
        panel.add(valuePanel)
    }

    override fun createCenterPanel(): JComponent {
        return panel
    }

    fun getSelectedValue(): Map.Entry<String, Any> {
        return when (currentCardName) {
            attributeEntity[0] -> mapOf(currentCardName to tableNameField.text).entries.first()
            attributeEntity[1] -> mapOf(currentCardName to primaryKeyList.selectedValuesList).entries.first()
            attributeEntity[2] -> mapOf(currentCardName to primaryKeyListModel.elements().toList()).entries.first()
            attributeEntity[3] -> mapOf(currentCardName to primaryKeyListModel.elements().toList()).entries.first()
            attributeEntity[4] -> mapOf(currentCardName to comboBoxInheritSuperIndices.selectedItem!!).entries.first()
            attributeEntity[5] -> mapOf(currentCardName to ignoredColumnsList.selectedValuesList).entries.first()
            else -> mapOf("key" to "value").entries.first()
        }
    }
}
