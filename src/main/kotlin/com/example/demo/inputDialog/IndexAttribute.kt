package com.example.demo.inputDialog

import com.example.demo.model.Column
import com.example.demo.model.PrimaryKey
import com.example.demo.tableConfig.ForeignKeyAttribute
import com.example.demo.tableConfig.IndexAttribute
import com.intellij.openapi.ui.DialogWrapper
import java.awt.BorderLayout
import java.awt.CardLayout
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class IndexAttribute(
    private val countValue: Int,
    selectedElement: String,
    listModelPrimaryKey: DefaultListModel<PrimaryKey>,
    listModelColumn: DefaultListModel<Column>
) : DialogWrapper(true) {
    private val panel = JPanel()

    private val panelMain = JPanel(CardLayout())

    private val attributeEntity = IndexAttribute().get()

    private var currentCardName: String = selectedElement

    // Panel for name
    private val indexNameField = JTextField(20)
    private val indexNamePanel = JPanel().apply {
        add(JLabel("Value:"))
        indexNameField.document.addDocumentListener(object : DocumentListener {
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
        add(indexNameField)
        isOKActionEnabled = false
    }

    private fun checkConditionsTableNamePanel() {
        val conditionsMet: Boolean = indexNameField.text.isNotEmpty()
        isOKActionEnabled = conditionsMet
    }

    // Panel for values
    private val valueListModel = DefaultListModel<String>()
    private val valueList = JList(valueListModel)
    private val valuesPanel = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)

        // Add a list of all rows
        listModelPrimaryKey.elements().toList().forEach { element ->
            valueListModel.addElement(element.name)
        }
        listModelColumn.elements().toList().forEach { element ->
            valueListModel.addElement(element.name)
        }

        valueList.addListSelectionListener {
            isOKActionEnabled = valueListModel.size() > 0
        }

        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.X_AXIS)
        add(JScrollPane(valueList))
        add(panel)
    }

    fun getValueCount(): Int {
        return valueList.selectedValuesList.size
    }

    // Panel for orders
    private val orderList = mutableListOf<String>()
    private val comboBoxes = mutableListOf<JComboBox<String>>()
    private val ordersPanel = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)

        for (i in 1..countValue) {
            val panelLabel = JPanel()
            panelLabel.layout = BoxLayout(panelLabel, BoxLayout.X_AXIS)

            val types = IndexAttribute().getAction()
            val comboBoxDateType = JComboBox(types)

            comboBoxes.add(comboBoxDateType)

            comboBoxDateType.addActionListener {
                val selectedItem = comboBoxDateType.selectedItem?.toString()

                if (selectedItem != null) {
                    if (orderList.size > i - 1) {
                        orderList[i - 1] = selectedItem
                    } else {
                        orderList.add(selectedItem)
                    }
                }

                isOKActionEnabled = comboBoxes.all { it.selectedItem != null }
            }

            panelLabel.add(JLabel("Orders: $i"))
            panelLabel.add(comboBoxDateType)
            add(panelLabel)
        }
    }



    // Panel for unique
    private val comboBoxUnique = JComboBox(arrayOf("true", "false"))
    private val uniquePanel = JPanel().apply {
        comboBoxUnique.addActionListener {
            isOKActionEnabled = comboBoxUnique.selectedItem != null
        }
        add(comboBoxUnique)
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


        panelMain.add(indexNamePanel, attributeEntity[0])
        panelMain.add(valuesPanel, attributeEntity[1])
        panelMain.add(ordersPanel, attributeEntity[2])
        panelMain.add(uniquePanel, attributeEntity[3])


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
            attributeEntity[0] -> mapOf(currentCardName to indexNameField.text).entries.first()
            attributeEntity[1] -> mapOf(currentCardName to valueList.selectedValuesList).entries.first()
            attributeEntity[2] -> mapOf(currentCardName to orderList).entries.first()
            attributeEntity[3] -> mapOf(currentCardName to comboBoxUnique.selectedItem!!).entries.first()
            else -> mapOf("key" to "value").entries.first()
        }
    }
}
