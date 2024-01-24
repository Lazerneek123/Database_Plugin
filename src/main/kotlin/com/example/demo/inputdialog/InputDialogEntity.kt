package com.example.demo.inputdialog

import com.intellij.openapi.ui.DialogWrapper
import java.awt.event.ItemEvent
import javax.swing.*

class InputDialogEntity : DialogWrapper(true) {
    private val panel = JPanel()

    private val nameLabel = JLabel("Table Name:")
    private val nameTableField = JTextField()

    private val primaryKeyLabel = JLabel("Primary Key")
    private val primaryKeyLabelName = JLabel("Name:")
    private val primaryKeyTextFieldName = JTextField()
    private val primaryKeyLabelValue = JLabel("Value:")
    private val primaryKeyTextFieldValue = JTextField()

    private val primaryKeyAutoGenerate = JCheckBox("AutoGenerate")
    private var isActionEnabled: Boolean = true

    init {
        title = "Create Table"
        init()

        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

        val nameTablePanel = JPanel()
        nameTablePanel.layout = BoxLayout(nameTablePanel, BoxLayout.X_AXIS)

        nameTablePanel.add(nameLabel)
        nameTablePanel.add(nameTableField)
        panel.add(nameTablePanel)

        // extra space between elements
        panel.add(Box.createVerticalStrut(10))

        val primaryKeyPanel = JPanel()
        primaryKeyPanel.layout = BoxLayout(primaryKeyPanel, BoxLayout.X_AXIS)
        primaryKeyPanel.add(primaryKeyLabel)
        panel.add(primaryKeyPanel)
        panel.add(Box.createVerticalStrut(5))

        val primaryKeyInputPanel = JPanel()
        primaryKeyInputPanel.layout = BoxLayout(primaryKeyInputPanel, BoxLayout.X_AXIS)
        primaryKeyInputPanel.add(primaryKeyLabelName)
        primaryKeyInputPanel.add(primaryKeyTextFieldName)
        primaryKeyInputPanel.add(primaryKeyLabelValue)
        primaryKeyInputPanel.add(primaryKeyTextFieldValue)

        // Додаємо слухача подій для checkBox
        primaryKeyAutoGenerate.isSelected = true
        primaryKeyTextFieldValue.isEnabled = false
        primaryKeyAutoGenerate.addItemListener { e ->
            if (e.stateChange == ItemEvent.SELECTED) {
                // Якщо checkBox відмічений, встановлюємо isActionEnabled на true
                isActionEnabled = true
                primaryKeyTextFieldValue.isEnabled = false
            } else if (e.stateChange == ItemEvent.DESELECTED) {
                // Якщо checkBox не відмічений, встановлюємо isActionEnabled на false
                isActionEnabled = false
                primaryKeyTextFieldValue.isEnabled = true
            }
        }
        primaryKeyInputPanel.add(primaryKeyAutoGenerate)

        panel.add(primaryKeyInputPanel)
    }

    override fun createCenterPanel(): JComponent {
        return panel
    }

    fun getTableName(): String {
        return nameTableField.text
    }

    fun getPrimaryKeyName(): String {
        return primaryKeyTextFieldName.text
    }

    fun getPrimaryKeyAutoGenerate(): Boolean {
        return isActionEnabled
    }


    fun getPrimaryKeyValue(): String {
        return primaryKeyTextFieldValue.text
    }
}
