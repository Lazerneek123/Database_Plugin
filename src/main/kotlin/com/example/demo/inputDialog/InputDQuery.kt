package com.example.demo.inputDialog

import com.example.demo.element.TextFieldRegex
import com.intellij.openapi.ui.DialogWrapper
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class InputDQuery : DialogWrapper(true) {
    private val panel = JPanel()

    private val nameLabel = JLabel("Name:")
    private val nameField = JTextField()
    private val queryLabel = JLabel("Value:")
    private val queryField = JTextField()

    private val typeLabel = JLabel("Type:")
    private val comboBoxQueryAnnotation = JComboBox(arrayOf("Transaction", "RewriteQueriesToDropUnusedColumns"))

    private var manualInput = false

    private val queryTypes = arrayOf(
        "Insert",
        "Update",
        "Delete",
        "AllUsers",
        "ListUsersEmpty",
        "SearchUsersByNameLetter",
        "Query"
    )

    // Creating a drop-down list
    private val comboBoxQueryTypes = JComboBox(queryTypes)

    init {
        title = "Create Query"
        init()

        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

        val nameColumnPanel = JPanel()
        nameColumnPanel.layout = BoxLayout(nameColumnPanel, BoxLayout.X_AXIS)

        nameColumnPanel.add(nameLabel)
        nameColumnPanel.add(nameField)
        checkTextFieldToLatinCharactersOnly()
        nameColumnPanel.add(queryLabel)
        nameColumnPanel.add(queryField)
        queryLabel.isVisible = false
        queryField.isVisible = false

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
        comboBoxQueryTypes.addActionListener {
            val selectedItem = comboBoxQueryTypes.selectedItem
            if (selectedItem as? String == "Query") {
                manualInput = true
                queryLabel.isVisible = true
                queryField.isVisible = true
            } else {
                manualInput = false
                queryLabel.isVisible = false
                queryField.isVisible = false
            }
            checkConditions()
        }

        comboBoxPanel.add(typeLabel)
        comboBoxPanel.add(comboBoxQueryTypes)
        panel.add(comboBoxPanel)

        val valuePanel = JPanel()
        valuePanel.layout = BoxLayout(valuePanel, BoxLayout.X_AXIS)

        comboBoxQueryAnnotation.isVisible = false
        valuePanel.add(comboBoxQueryAnnotation)
        comboBoxQueryAnnotation.addActionListener {
            checkConditions()
        }

        val valueAutoPanel = JPanel()
        valueAutoPanel.layout = BoxLayout(valueAutoPanel, BoxLayout.Y_AXIS)

        valuePanel.add(valueAutoPanel)

        isOKActionEnabled = false
        panel.add(valuePanel)
    }

    private fun checkConditions() {
        val name = nameField.text
        // Condition check
        val conditionsMet: Boolean =
            if (comboBoxQueryAnnotation.isEditable || comboBoxQueryAnnotation.isEnabled) {
                name.isNotEmpty()
            } else {
                name.isNotEmpty()
            }

        // Activate or deactivate the OK button
        isOKActionEnabled = conditionsMet
    }

    private fun checkTextFieldToLatinCharactersOnly() {
        nameField.document = TextFieldRegex(nameField.document).setTextFieldToLatinCharactersOnly()
    }

    override fun createCenterPanel(): JComponent {
        return panel
    }

    fun getName(): String {
        return nameField.text
    }

    fun getQueryType(): String {
        return comboBoxQueryTypes.selectedItem as String
    }

    fun getManualInput(): Boolean {
        return manualInput
    }

    fun getValueQuery(): String? {
        return if(queryField.isVisible){
            queryField.text
        }
        else{
            null
        }
    }
}
