package com.example.demo.input_dialog

import com.intellij.openapi.ui.DialogWrapper
import javax.swing.*

class InputDialogColumn : DialogWrapper(true) {
    private val panel = JPanel()

    private val nameLabel = JLabel("Name:")
    private val nameField = JTextField()

    private val valueLabel = JLabel("Value:")
    private val valueField = JTextField()

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
    private var comboBox = JComboBox(dataTypes)

    init {
        title = "Create Column"
        init()

        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

        val nameColumnPanel = JPanel()
        nameColumnPanel.layout = BoxLayout(nameColumnPanel, BoxLayout.X_AXIS)

        nameColumnPanel.add(nameLabel)
        nameColumnPanel.add(nameField)
        panel.add(nameColumnPanel)

        val comboBoxPanel = JPanel()
        comboBoxPanel.layout = BoxLayout(comboBoxPanel, BoxLayout.X_AXIS)

        comboBoxPanel.add(comboBox)
        panel.add(comboBoxPanel)

        val valuePanel = JPanel()
        valuePanel.layout = BoxLayout(valuePanel, BoxLayout.X_AXIS)

        valuePanel.add(valueLabel)
        valuePanel.add(valueField)
        panel.add(valuePanel)
    }

    override fun createCenterPanel(): JComponent {
        return panel
    }

    fun getColumnName(): String {
        return nameField.text
    }

    fun getColumnDataType(): String {
        return comboBox.selectedItem as String
    }

    fun getColumnValue(): String {
        return valueField.text
    }
}
