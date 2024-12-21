package com.example.demo.inputDialog.database

import com.example.demo.element.TextFieldRegex
import com.example.demo.inputDialog.table.InputDColumn
import com.example.demo.inputDialog.table.InputDColumnInfo
import com.example.demo.inputDialog.table.InputDEntityAttribute
import com.example.demo.inputDialog.table.InputDPrimaryKey
import com.example.demo.model.Column
import com.example.demo.model.ColumnAdvancedSettings
import com.example.demo.model.EntityAdvancedSettings
import com.example.demo.model.ForeignKey
import com.example.demo.model.Index
import com.example.demo.model.PrimaryKey
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import java.awt.BorderLayout
import java.awt.CardLayout
import java.awt.event.ActionEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.event.ListDataEvent
import javax.swing.event.ListDataListener

class InputDDataBase(private val directoryPath: String, private val event: AnActionEvent) : DialogWrapper(true) {
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

    private val listModelColumnsAttribute = DefaultListModel<ColumnAdvancedSettings?>()
    private val listColumnsAttribute = JList(listModelColumnsAttribute)
    fun getListModelColumnsAttribute(): DefaultListModel<ColumnAdvancedSettings?> {
        return listModelColumnsAttribute
    }

    private var listModelEntityAttribute = DefaultListModel<Map.Entry<String, Any?>?>()
    private val listEntityAttribute = JList(listModelEntityAttribute)
    fun getListModelEntityAttribute(): EntityAdvancedSettings? {
        return if (isEntity) {
            EntityAdvancedSettings(
                listModelEntityAttribute.elements().toList()[0]!!.value as String?,
                listModelEntityAttribute.elements().toList()[1]!!.value as List<String?>?,
                listModelEntityAttribute.elements().toList()[2]!!.value as List<ForeignKey?>?,
                listModelEntityAttribute.elements().toList()[3]!!.value as List<Index?>?,
                listModelEntityAttribute.elements().toList()[4]!!.value as String?,
                listModelEntityAttribute.elements().toList()[5]!!.value as List<String?>?
            )
        } else {
            null
        }
    }

    private val listModelColumn = DefaultListModel<Column>()
    private val listColumn = JList(listModelColumn)

    private var isColumnInfo = false
    fun isColumnInfo(): Boolean {
        return isColumnInfo
    }

    private var isEntity = false

    init {
        title = "Create Table"
        init()

        // Додаємо панелі для кроків
        panelMain.add(createStepMainPanel(), "Step 1")
        panelMain.add(createStepEntityPanel(), "Step 2")
        panelMain.add(createStepPrimaryKeysPanel(), "Step 3")
        panelMain.add(createStepColumnsPanel(), "Step 4")
    }

    private fun createStepColumnsPanel(): JPanel {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

        val nameLabel = JLabel("Columns")
        val panelLabel = JPanel()
        panelLabel.layout = BoxLayout(panelLabel, BoxLayout.X_AXIS)
        panelLabel.add(nameLabel)
        panel.add(panelLabel, BorderLayout.CENTER)

        listModelColumnsAttribute.addListDataListener(object : ListDataListener {
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

        listColumnsAttribute.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (e.clickCount == 1) {
                    val indexColumn = listColumnsAttribute.locationToIndex(e.point)
                    if (indexColumn >= 0) {
                        val selectedElement = listColumnsAttribute.model.getElementAt(indexColumn)
                        val inputDialog = InputDColumnInfo(selectedElement!!)
                        inputDialog.show()

                        // Get the results when you click the OK button
                        if (inputDialog.isOK) {
                            val columnInfo = inputDialog.getSelectedValue()
                            listModelColumnsAttribute.elements().toList().forEachIndexed { index, e ->
                                if (e!!.name == columnInfo.name) {
                                    listModelColumnsAttribute.set(index, columnInfo)
                                }
                            }
                            isColumnInfo = inputDialog.hasModelChanged()
                        }
                    }
                }
            }
        })

        // Adding a list to a panel
        panel.add(JScrollPane(listColumnsAttribute), BorderLayout.CENTER)

        // Adding buttons to a panel
        val buttonPanel = JPanel()
        panel.add(buttonPanel, BorderLayout.SOUTH)

        return panel
    }

    private fun createStepPrimaryKeysPanel(): JPanel {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

        val listModelPrimaryKeysAttribute = DefaultListModel<Map.Entry<String, Any?>>()
        val listPrimaryKeysAttribute = JList(listModelPrimaryKeysAttribute)

        val nameLabel = JLabel("Primary Keys")
        val panelLabel = JPanel()
        panelLabel.layout = BoxLayout(panelLabel, BoxLayout.X_AXIS)
        panelLabel.add(nameLabel)
        panel.add(panelLabel, BorderLayout.CENTER)


        /*var selectedElement = ""

        attribute.forEach { attributeName -> listModelPrimaryKeysAttribute.addElement(mapOf(attributeName to "").entries.first()) }

        panel.add(Box.createVerticalStrut(5))
        val panelPrimaryKey = JPanel()
        panelPrimaryKey.layout = BoxLayout(panelPrimaryKey, BoxLayout.Y_AXIS)
        panel.add(panelPrimaryKey)
        panel.add(Box.createVerticalStrut(5))

        // Add a change listener to the list model
        listModelPrimaryKeysAttribute.addListDataListener(object : ListDataListener {
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
        listPrimaryKeysAttribute.addListSelectionListener { event ->
            if (!event.valueIsAdjusting) {
                val element = listPrimaryKeysAttribute.selectedValue
                if (element != null) {
                    selectedElement = element.key
                    // Click your own InputDialog
                    val entityAdvancedSettings =
                        EntityAttribute(selectedElement, listModelPrimaryKey, listModelColumn, directoryPath)
                    entityAdvancedSettings.show()

                    // Get the results when you click the OK button
                    if (entityAdvancedSettings.isOK) {
                        val pair = entityAdvancedSettings.getSelectedValue()
                        listModelPrimaryKeysAttribute.elements().toList().forEachIndexed { index, element ->
                            if (element.key == pair.key) {
                                val updatedElement = mapOf(element.key to pair.value).entries.first()
                                listModelPrimaryKeysAttribute.set(index, updatedElement)
                            }
                        }
                    }
                }
            }
        }

        // Adding a list to a panel
        panel.add(JScrollPane(listPrimaryKeysAttribute), BorderLayout.CENTER)*/

        // Adding buttons to a panel
        val buttonPanel = JPanel()
        panel.add(buttonPanel, BorderLayout.SOUTH)

        return panel
    }

    fun setEntityName() {
        val updatedElement = mapOf(attribute[0] to nameTableField.text).entries.first()
        listModelEntityAttribute.set(0, updatedElement)
    }

    private fun createStepEntityPanel(): JPanel {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

        val nameLabel = JLabel("Entity")
        val panelLabel = JPanel()
        panelLabel.layout = BoxLayout(panelLabel, BoxLayout.X_AXIS)
        panelLabel.add(nameLabel)
        panel.add(panelLabel, BorderLayout.CENTER)


        var selectedElement = ""

        attribute.forEach { attributeName -> listModelEntityAttribute.addElement(mapOf(attributeName to null).entries.first()) }

        panel.add(Box.createVerticalStrut(5))
        val panelPrimaryKey = JPanel()
        panelPrimaryKey.layout = BoxLayout(panelPrimaryKey, BoxLayout.Y_AXIS)
        panel.add(panelPrimaryKey)
        panel.add(Box.createVerticalStrut(5))

        // Add a change listener to the list model
        listEntityAttribute.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (e.clickCount == 1) {
                    val element = listEntityAttribute.selectedValue
                    if (element != null) {
                        selectedElement = element.key

                        val inputDialog =
                            InputDEntityAttribute(selectedElement, listModelPrimaryKey, listModelColumn, directoryPath)
                        inputDialog.show()

                        // Get the results when you click the OK button
                        if (inputDialog.isOK) {
                            val pair = inputDialog.getSelectedValue()
                            listModelEntityAttribute.elements().toList().forEachIndexed { index, element ->
                                // Update element if key matches
                                if (element!!.key == pair.key) {
                                    val updatedElement = mapOf(element.key to pair.value).entries.first()
                                    listModelEntityAttribute.set(index, updatedElement)
                                }

                                // Checking for differences in values
                                val currentValue = listModelEntityAttribute.get(index)!!.value
                                val initialValue = inputDialog.getInitialValues()[index]!!.value
                                if (initialValue != currentValue) {
                                    isEntity = true
                                    return@forEachIndexed // Stop the loop if a difference is found
                                }
                            }
                        }

                    }
                }
            }
        })
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
        panel.add(buttonPanel, BorderLayout.SOUTH)

        return panel
    }

    var str = ""

    fun isEntity(): Boolean {
        return isEntity
    }

    private fun createStepMainPanel(): JPanel {
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
            val inputDColumn = InputDColumn()
            inputDColumn.show()

            // Get the results when you click the OK button
            if (inputDColumn.isOK) {
                val columnName = inputDColumn.getColumnName()

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
                    val dataType = inputDColumn.getColumnDataType()
                    val value = inputDColumn.getColumnValue()
                    val nullable = inputDColumn.getColumnNullable()

                    val newColumn = Column(columnName, dataType, value, nullable)
                    listModelColumn.addElement(newColumn)
                    listModelColumnsAttribute.clear()
                    listModelColumn.elements().toList().forEach { column ->
                        listModelColumnsAttribute.addElement(
                            ColumnAdvancedSettings(
                                column.name,
                                null,
                                null,
                                null,
                                null
                            )
                        )
                    }
                }
            }
        }

        // Delete button
        val removeBtnColumn = JButton("Delete")
        removeBtnColumn.addActionListener {
            val selectedIndex = listColumn.selectedIndex
            if (selectedIndex != -1) {
                listModelColumn.removeElementAt(selectedIndex)
                listModelColumnsAttribute.removeElementAt(selectedIndex)
            }
        }

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
            val inputDPrimaryKey = InputDPrimaryKey()
            inputDPrimaryKey.show()

            // Get the results when you click the OK button
            if (inputDPrimaryKey.isOK) {
                val columnName = inputDPrimaryKey.getPrimaryKeyName()

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
                    val dataType = inputDPrimaryKey.getPrimaryKeyDataType()
                    val value = inputDPrimaryKey.getPrimaryKeyValue()
                    val autoGenerator = inputDPrimaryKey.getPrimaryKeyAutoGenerate()

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
        isOKActionEnabled = false

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
                setEntityName()
                checkConditions()
            }

            override fun removeUpdate(e: DocumentEvent) {
                setEntityName()
                checkConditions()
            }

            override fun changedUpdate(e: DocumentEvent) {
                setEntityName()
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

        createAction.isEnabled = conditionsMet
        previousAction.isEnabled = conditionsMet
        nextAction.isEnabled = conditionsMet
    }

    private fun setTextFieldToLatinCharactersOnly() {
        nameTableField.document = TextFieldRegex(nameTableField.document).setTextFieldToLatinCharactersOnly()
    }

    override fun createCenterPanel(): JComponent {
        return panelMain
    }

    fun getName(): String {
        return nameTableField.text.toString()
    }

    fun getPrimaryKeysData(): List<PrimaryKey> {
        return listModelPrimaryKey.elements().toList()
    }

    fun getColumnsData(): List<Column> {
        return listModelColumn.elements().toList()
    }

    private lateinit var createAction: Action
    private lateinit var nextAction: Action
    private lateinit var previousAction: Action


    override fun createActions(): Array<Action> {
        createAction = object : DialogWrapperAction("Create") {
            override fun doAction(e: ActionEvent?) {
                /*val groupId = "Custom Notification Group"
                NotificationGroupManager.getInstance().getNotificationGroup(groupId)
                    .createNotification("Database class", str, NotificationType.INFORMATION).notify(event.project)*/
                close(OK_EXIT_CODE)
            }
        }
        createAction.isEnabled = false

        previousAction = object : DialogWrapperAction("Previous") {
            override fun doAction(e: ActionEvent?) {
                cardLayout.previous(panelMain)
            }
        }
        previousAction.isEnabled = false

        nextAction = object : DialogWrapperAction("Next") {
            override fun doAction(e: ActionEvent?) {
                cardLayout.next(panelMain)
            }
        }
        nextAction.isEnabled = false
        return arrayOf(
            previousAction,
            nextAction,
            createAction,
            cancelAction
        )
    }
}