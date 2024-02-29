package com.example.demo.input

import com.example.demo.element.TextFieldValueColumn
import com.intellij.openapi.ui.DialogWrapper
import java.awt.event.ItemEvent
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.text.AttributeSet
import javax.swing.text.PlainDocument


class InputDialogColumn : DialogWrapper(true) {
    private val panel = JPanel()

    private val nameLabel = JLabel("Name:")
    private val nameField = JTextField()

    private val valueLabel = JLabel("Value:")
    private val valueField = TextFieldValueColumn()
    private val typeLabel = JLabel("Type:")
    private val comboBoxBoolean = JComboBox(arrayOf("true", "false"))

    private val valueAutoGenerateCheckBox = JCheckBox("Auto Generate")
    private val nullableCheckBox = JCheckBox("Nullable")


    // Creating options for a drop-down list
    private val dataTypes = arrayOf(
        "String",
        "Int",
        "Double",
        "Long",
        "Boolean",
        "Float",
        "Byte"
    )

    // Creating a drop-down list
    private val comboBoxDateType = JComboBox(dataTypes)

    init {
        title = "Create Column"
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
                comboBoxBoolean.isEnabled = !valueAutoGenerateCheckBox.isSelected && !nullableCheckBox.isSelected
                comboBoxBoolean.isVisible = true
                valueField.isVisible = false
            } else {
                // To clear the data in the value term
                valueField.setSelectedItem(selectedItem as? String)
                comboBoxBoolean.isVisible = false
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

        comboBoxBoolean.isVisible = false
        valuePanel.add(comboBoxBoolean)
        comboBoxBoolean.addActionListener {
            checkConditions()
        }

        val valueAutoPanel = JPanel()
        valueAutoPanel.layout = BoxLayout(valueAutoPanel, BoxLayout.Y_AXIS)

        valueAutoGenerateCheckBox.isSelected = true
        valueField.isEnabled = false
        // We add an event listener for the checkBox
        valueAutoGenerateCheckBox.addItemListener { e ->
            if (e.stateChange == ItemEvent.SELECTED) {
                // If the checkBox is checked, set isActionEnabled to true
                comboBoxBoolean.isEnabled =
                    !valueAutoGenerateCheckBox.isSelected && !nullableCheckBox.isSelected && comboBoxDateType.selectedItem == "Boolean"
                comboBoxBoolean.isVisible = comboBoxDateType.selectedItem == "Boolean"
                valueField.isEnabled = false
                nullableCheckBox.isSelected = false
            } else if (e.stateChange == ItemEvent.DESELECTED) {
                // If the checkBox is not checked, set isActionEnabled to false
                comboBoxBoolean.isEnabled =
                    !valueAutoGenerateCheckBox.isSelected && !nullableCheckBox.isSelected && comboBoxDateType.selectedItem == "Boolean"
                comboBoxBoolean.isVisible = comboBoxDateType.selectedItem == "Boolean"
                valueField.isEnabled = true
            }
            checkConditions()
        }
        valueAutoPanel.add(valueAutoGenerateCheckBox)


        nullableCheckBox.isSelected = false
        // We add an event listener for the checkBox
        nullableCheckBox.addItemListener { e ->
            if (e.stateChange == ItemEvent.SELECTED) {
                comboBoxBoolean.isEnabled =
                    !valueAutoGenerateCheckBox.isSelected && !nullableCheckBox.isSelected && comboBoxDateType.selectedItem == "Boolean"
                comboBoxBoolean.isVisible = comboBoxDateType.selectedItem == "Boolean"
                valueField.isEnabled = false
                valueAutoGenerateCheckBox.isSelected = false
            } else if (e.stateChange == ItemEvent.DESELECTED) {
                comboBoxBoolean.isEnabled =
                    !valueAutoGenerateCheckBox.isSelected && !nullableCheckBox.isSelected && comboBoxDateType.selectedItem == "Boolean"
                comboBoxBoolean.isVisible = comboBoxDateType.selectedItem == "Boolean"
                valueField.isEnabled = true
            }
            checkConditions()
        }

        valueAutoPanel.add(nullableCheckBox)
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
            if (valueAutoGenerateCheckBox.isSelected || nullableCheckBox.isSelected || comboBoxBoolean.isEnabled || !valueField.isEnabled) {
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
        nameField.document = object : PlainDocument() {
            override fun insertString(offset: Int, str: String?, attr: AttributeSet?) {
                if (str == null) return
                if (str.matches(Regex("[a-zA-Z]*"))) {
                    super.insertString(offset, str, attr)
                }
            }
        }
    }

    override fun createCenterPanel(): JComponent {
        return panel
    }

    fun getColumnName(): String {
        return nameField.text
    }

    fun getColumnDataType(): String {
        return comboBoxDateType.selectedItem as String
    }

    fun getColumnNullable(): Boolean {
        return nullableCheckBox.isSelected
    }

    fun getColumnValue(): String {
        if (!nullableCheckBox.isSelected) {
            if (comboBoxBoolean.isVisible) {
                return comboBoxBoolean.selectedItem as String
            } else {
                if (comboBoxDateType.selectedItem as String == "String") {
                    return '"' + valueField.text + '"'
                }
                if (valueAutoGenerateCheckBox.isSelected) {
                    return autoGenerateValue()
                }
                return valueField.text
            }
        } else {
            return "null"
        }
    }

    private fun autoGenerateValue(): String {
        return when (comboBoxDateType.selectedItem) {
            "String" -> """"""
            "Int", "Long", "Byte" -> "0"
            "Double" -> "0.0"
            "Boolean" -> "true"
            "Float" -> "0.0f"
            // Other options to choose from
            else -> ""
        }
    }
}
