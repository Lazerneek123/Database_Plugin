package com.example.demo.inputDialog.dao

import com.example.demo.element.TextFieldRegex
import com.intellij.openapi.ui.DialogWrapper
import java.awt.Color
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class InputDQuery : DialogWrapper(true) {
    private val panel = JPanel()

    private val nameLabel = JLabel("Name:")
    private val nameField = JTextField()
    private val queryLabel = JLabel("Value:")
    private val queryArea = JTextArea(3, 30)
    private val scrollPane = JScrollPane(queryArea)

    private val typeLabel = JLabel("Type:")
    private val comboBoxQueryAnnotation = JComboBox(arrayOf("Transaction", "RewriteQueriesToDropUnusedColumns"))

    private val listOnConflict = arrayOf(
        "",
        "REPLACE",
        "IGNORE",
        "ABORT",
        "NONE"
    )
    private val onConflictLabel = JLabel("Attribute conflict:")
    private val comboBoxQueryOnConflict = JComboBox(listOnConflict)

    private var manualInput = false

    private val queryTypes = arrayOf(
        "Insert",
        "Update",
        "Delete",
        "AllEntity",
        "ListEntitysEmpty",
        "SearchEntitysByNameLetter",
        "Query"
    )

    // Creating a drop-down list
    private val comboBoxQueryTypes = JComboBox(queryTypes)

    init {
        title = "Create Query"
        init()

        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

        val namePanel = JPanel()
        namePanel.layout = BoxLayout(namePanel, BoxLayout.X_AXIS)

        namePanel.add(nameLabel)
        namePanel.add(nameField)
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
        panel.add(namePanel)

        val comboBoxPanel = JPanel()
        comboBoxPanel.layout = BoxLayout(comboBoxPanel, BoxLayout.X_AXIS)

        comboBoxPanel.add(typeLabel)
        comboBoxPanel.add(comboBoxQueryTypes)
        panel.add(comboBoxPanel)

        val valuePanel = JPanel()
        valuePanel.layout = BoxLayout(valuePanel, BoxLayout.X_AXIS)

        valuePanel.isVisible = false
        valuePanel.add(comboBoxQueryAnnotation)
        comboBoxQueryAnnotation.addActionListener {
            checkConditions()
        }

        val onConflictPanel = JPanel()
        onConflictPanel.layout = BoxLayout(onConflictPanel, BoxLayout.X_AXIS)

        onConflictPanel.isVisible = false
        onConflictPanel.add(onConflictLabel)
        onConflictPanel.add(comboBoxQueryOnConflict)
        comboBoxQueryOnConflict.addActionListener {
            checkConditions()
        }

        val valueAutoPanel = JPanel()
        valueAutoPanel.layout = BoxLayout(valueAutoPanel, BoxLayout.Y_AXIS)

        valuePanel.add(valueAutoPanel)

        isOKActionEnabled = false
        panel.add(valuePanel)
        panel.add(onConflictPanel)

        val queryPanel = JPanel()
        queryPanel.layout = BoxLayout(queryPanel, BoxLayout.X_AXIS)

        // Listener for JComboBox
        comboBoxQueryTypes.addActionListener {
            val selectedItem = comboBoxQueryTypes.selectedItem
            if (selectedItem as? String == "Query") {
                manualInput = false
                valuePanel.isVisible = true
                queryPanel.isVisible = true
                scrollPane.isVisible = true
                onConflictPanel.isVisible = false
            } else {
                manualInput = false
                valuePanel.isVisible = false
                queryPanel.isVisible = false
                scrollPane.isVisible = false
                onConflictPanel.isVisible = true
            }
            checkConditions()
        }

        queryLabel.isVisible = false
        queryArea.isVisible = false
        queryArea.lineWrap = true // Перенесення рядків
        queryArea.wrapStyleWord = true // Переносить за словами
        queryArea.border = BorderFactory.createLineBorder(Color.GRAY) // Рамка навколо тексту

        queryPanel.add(queryLabel)
        queryPanel.add(scrollPane)
        panel.add(queryPanel)
    }

    private fun checkConditions() {
        val name = nameField.text
        // Condition check
        val conditionsMet: Boolean =
            if (comboBoxQueryAnnotation.isEditable || comboBoxQueryAnnotation.isEnabled || comboBoxQueryOnConflict.isEnabled) {
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
        return if (queryArea.isVisible) {
            queryArea.text
        } else {
            null
        }
    }

    fun getOnConflict(): String? {
        return if (comboBoxQueryOnConflict.isVisible && comboBoxQueryOnConflict.selectedItem != "") {
            comboBoxQueryOnConflict.selectedItem!!.toString()
        } else {
            null
        }
    }
}
