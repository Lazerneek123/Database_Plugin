package com.example.demo.input

import com.example.demo.element.TextFieldRegex
import com.example.demo.element.TextFieldValuePrimaryKey
import com.intellij.openapi.ui.DialogWrapper
import java.awt.event.ItemEvent
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class InputDialogPrimaryKey : DialogWrapper(true) {
    private val panel = JPanel()

    private val nameLabel = JLabel("Name:")
    private val nameField = JTextField()

    private val valueLabel = JLabel("Value:")
    private val valueField = TextFieldValuePrimaryKey()
    private val typeLabel = JLabel("Type:")

    // Creating options for a drop-down list
    private val dataTypes = arrayOf(
        "String",
        "Int",
        "Long",
        "Byte"
    )

    // Creating a drop-down list
    private val comboBoxDateType = JComboBox(dataTypes)

    private val valueAutoGenerateCheckBox = JCheckBox("Auto Generate")

    init {
        title = "Create Primary Key"
        init()

        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

        val nameColumnPanel = JPanel()
        nameColumnPanel.layout = BoxLayout(nameColumnPanel, BoxLayout.X_AXIS)

        nameColumnPanel.add(nameLabel)
        nameColumnPanel.add(nameField)
        checkTextFieldToLatinCharactersOnly()

        // Document listener for a text field
        nameField.document.addDocumentListener(object : DocumentListener {
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
        panel.add(nameColumnPanel)

        val comboBoxPanel = JPanel()
        comboBoxPanel.layout = BoxLayout(comboBoxPanel, BoxLayout.X_AXIS)

        // Listener for JComboBox
        comboBoxDateType.addActionListener {
            val selectedItem = comboBoxDateType.selectedItem
            if (selectedItem as? String == "Boolean") {
                valueField.isVisible = false
            } else {
                // To clear the data in the value term
                valueField.setSelectedItem(selectedItem as? String)
                valueField.isVisible = true
                updateValueField()
            }
            checkConditions()
        }

        comboBoxPanel.add(typeLabel)
        comboBoxPanel.add(comboBoxDateType)
        panel.add(comboBoxPanel)

        val valuePanel = JPanel()
        valuePanel.layout = BoxLayout(valuePanel, BoxLayout.X_AXIS)

        valuePanel.add(valueLabel)
        valuePanel.add(valueField)

        val valueAutoPanel = JPanel()
        valueAutoPanel.layout = BoxLayout(valueAutoPanel, BoxLayout.Y_AXIS)

        valueAutoGenerateCheckBox.isSelected = true
        valueField.isEnabled = false
        // We add an event listener for the checkBox
        valueAutoGenerateCheckBox.addItemListener { e ->
            if (e.stateChange == ItemEvent.SELECTED) {
                // If the checkBox is checked, set isActionEnabled to true
                valueField.isEnabled = false
            } else if (e.stateChange == ItemEvent.DESELECTED) {
                // If the checkBox is not checked, set isActionEnabled to false
                valueField.isEnabled = true
            }
            checkConditions()
        }
        valueAutoPanel.add(valueAutoGenerateCheckBox)

        valuePanel.add(valueAutoPanel)

        isOKActionEnabled = false
        updateValueField()
        panel.add(valuePanel)
    }

    private fun checkConditions() {
        val name = nameField.text
        val value = valueField.text

        // Condition check
        val conditionsMet: Boolean =
            if (valueAutoGenerateCheckBox.isSelected || !valueField.isEnabled) {
                name.isNotEmpty()
            } else {
                name.isNotEmpty() && value.isNotEmpty()
            }

        // Activate or deactivate the OK button
        isOKActionEnabled = conditionsMet
    }

    private fun updateValueField() {
        valueField.document.addDocumentListener(object : DocumentListener {
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


    private fun checkTextFieldToLatinCharactersOnly() {
        nameField.document = TextFieldRegex(nameField.document).setTextFieldToLatinCharactersOnly()
    }

    override fun createCenterPanel(): JComponent {
        return panel
    }

    fun getPrimaryKeyName(): String {
        return nameField.text
    }

    fun getPrimaryKeyDataType(): String {
        return comboBoxDateType.selectedItem as String
    }

    fun getPrimaryKeyValue(): String {
        if (valueAutoGenerateCheckBox.isSelected) {
            return autoGenerateValue()
        } else {
            if (comboBoxDateType.selectedItem as String == "String") {
                return '"' + valueField.text + '"'
            }
            return valueField.text
        }
    }

    fun getPrimaryKeyAutoGenerate(): Boolean {
        return valueAutoGenerateCheckBox.isSelected
    }

    private fun autoGenerateValue(): String {
        return when (comboBoxDateType.selectedItem) {
            "String" -> """"""""
            "Int", "Long", "Byte" -> "0"
            // Other options to choose from
            else -> ""
        }
    }
}
