package com.example.demo.inputDialog

import com.example.demo.tableConfig.ColumnAttribute
import com.intellij.openapi.ui.DialogWrapper
import java.awt.CardLayout
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class ColumnAttribute(
    selectedElement: String
) : DialogWrapper(true) {
    private val panel = JPanel()

    private val panelMain = JPanel(CardLayout())

    private val attributeColumn = ColumnAttribute().get()
    private var currentCardName: String = selectedElement

    // Panel for column name
    private val columnNameField = JTextField(20)
    private val columnNamePanel = JPanel().apply {
        add(JLabel("Value:"))
        columnNameField.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent) {
                checkConditionsPanel(columnNameField)
            }

            override fun removeUpdate(e: DocumentEvent) {
                checkConditionsPanel(columnNameField)
            }

            override fun changedUpdate(e: DocumentEvent) {
                checkConditionsPanel(columnNameField)
            }
        })
        add(columnNameField)
        isOKActionEnabled = false
    }

    private fun checkConditionsPanel(field: JTextField) {
        val conditionsMet: Boolean = field.text.isNotEmpty()
        isOKActionEnabled = conditionsMet
    }

    // Panel for type affinity
    private val comboBoxTypeAffinity = JComboBox(ColumnAttribute().getDataType())
    private val typeAffinityPanel = JPanel().apply {
        comboBoxTypeAffinity.addActionListener {
            isOKActionEnabled = comboBoxTypeAffinity.selectedItem != null
        }
        add(comboBoxTypeAffinity)
    }

    // Panel for index
    private val comboBoxIndex = JComboBox(arrayOf("true", "false"))
    private val indexPanel = JPanel().apply {
        comboBoxIndex.addActionListener {
            isOKActionEnabled = comboBoxIndex.selectedItem != null
        }
        add(comboBoxIndex)
    }

    // Panel for default value
    private val defaultValueField = JTextField(20)
    private val defaultValuePanel = JPanel().apply {
        add(JLabel("Value:"))
        defaultValueField.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent) {
                checkConditionsPanel(defaultValueField)
            }

            override fun removeUpdate(e: DocumentEvent) {
                checkConditionsPanel(defaultValueField)
            }

            override fun changedUpdate(e: DocumentEvent) {
                checkConditionsPanel(defaultValueField)
            }
        })
        add(defaultValueField)
        isOKActionEnabled = false
    }

    // Panel for collate
    private val comboBoxCollate = JComboBox(ColumnAttribute().getComparativeSchema())
    private val collatePanel = JPanel().apply {
        comboBoxCollate.addActionListener {
            isOKActionEnabled = comboBoxCollate.selectedItem != null
        }
        add(comboBoxCollate)
    }

    init {
        title = currentCardName
        init()
        isOKActionEnabled = false

        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

        val nameColumnPanel = JPanel()
        nameColumnPanel.layout = BoxLayout(nameColumnPanel, BoxLayout.X_AXIS)

        panel.add(nameColumnPanel)

        val comboBoxPanel = JPanel()
        comboBoxPanel.layout = BoxLayout(comboBoxPanel, BoxLayout.X_AXIS)
        panelMain.add(comboBoxPanel)

        val valuePanel = JPanel()
        valuePanel.layout = BoxLayout(valuePanel, BoxLayout.X_AXIS)


        panelMain.add(columnNamePanel, attributeColumn[0])
        panelMain.add(typeAffinityPanel, attributeColumn[1])
        panelMain.add(indexPanel, attributeColumn[2])
        panelMain.add(defaultValuePanel, attributeColumn[3])
        panelMain.add(collatePanel, attributeColumn[4])


        val cl = panelMain.layout as CardLayout
        cl.show(panelMain, currentCardName)


        // Основна панель
        val mainPanel = JPanel()
        mainPanel.layout = BoxLayout(mainPanel, BoxLayout.Y_AXIS)
        mainPanel.add(panelMain)

        panel.add(mainPanel)

        panel.add(valuePanel)
    }

    override fun createCenterPanel(): JComponent {
        return panel
    }

    fun getSelectedValue(): Map.Entry<String, String> {
        return when (currentCardName) {
            attributeColumn[0] -> mapOf(currentCardName to columnNameField.text).entries.first()
            attributeColumn[1] -> mapOf(currentCardName to comboBoxTypeAffinity.selectedItem!!.toString()).entries.first()
            attributeColumn[2] -> mapOf(currentCardName to comboBoxIndex.selectedItem!!.toString()).entries.first()
            attributeColumn[3] -> mapOf(currentCardName to defaultValueField.text).entries.first()
            attributeColumn[4] -> mapOf(currentCardName to comboBoxCollate.selectedItem!!.toString()).entries.first()
            else -> mapOf("key" to "value").entries.first()
        }
    }
}
