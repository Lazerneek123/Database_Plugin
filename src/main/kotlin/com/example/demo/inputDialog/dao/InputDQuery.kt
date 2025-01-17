package com.example.demo.inputDialog.dao

import com.example.demo.element.TextFieldRegex
import com.example.demo.model.Query
import com.example.demo.model.QueryType
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.JBColor
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import java.awt.Dimension
import java.awt.FlowLayout
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class InputDQuery(
    private val listQuery: JList<Query>,
    private val ktFile: KtFile?
) : DialogWrapper(true) {
    private val panel = JPanel()

    private val nameLabel = JLabel("Name:")
    private val nameField = JTextField(10)

    private val queryLabel = JLabel("Enter a query")
    private val queryArea = JTextArea(6, 30)

    private val scrollPane = JScrollPane(queryArea)

    private val typeLabel = JLabel("Type:")

    private val listOnConflict = arrayOf(
        "",
        "REPLACE",
        "IGNORE",
        "ABORT",
        "NONE"
    )
    private val onConflictLabel = JLabel("Attribute conflict:")
    private val comboBoxQueryOnConflict = JComboBox(listOnConflict)

    private var listColumn = arrayOf("")
    private val columnLabel = JLabel("Column:")
    private val comboBoxQueryColumn = JComboBox(listColumn)

    private var queryType = QueryType.TEMPLATE

    private val queryCategories = arrayOf(
        "Insert",
        "Update",
        "Delete",
        "AllEntity",
        "ListEntitiesEmpty",
        "SearchByLetter",
        "Query"
    )

    // Creating a drop-down list
    private val comboBoxQueryCategories = JComboBox(queryCategories)

    private var columnSelect: String? = ""

    init {
        title = "Create Query"
        init()

        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

        val namePanel = JPanel(FlowLayout(FlowLayout.LEFT))
        //namePanel.layout = BoxLayout(namePanel, BoxLayout.X_AXIS)

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

        val onConflictPanel = JPanel(FlowLayout(FlowLayout.LEFT))
        onConflictPanel.isVisible = false
        onConflictPanel.add(onConflictLabel)
        onConflictPanel.add(comboBoxQueryOnConflict)
        comboBoxQueryOnConflict.addActionListener {
            checkConditions()
        }

        val columnPanel = JPanel(FlowLayout(FlowLayout.LEFT))
        columnPanel.isVisible = false
        columnPanel.add(columnLabel)
        columnPanel.add(comboBoxQueryColumn)
        comboBoxQueryColumn.addActionListener {
            val selectedItem = comboBoxQueryColumn.selectedItem
            if (selectedItem != null) {
                columnSelect = selectedItem.toString()
            }
            checkConditions()
        }

        val comboBoxPanel = JPanel(FlowLayout(FlowLayout.LEFT))
        //comboBoxPanel.layout = BoxLayout(comboBoxPanel, BoxLayout.X_AXIS)

        comboBoxPanel.add(typeLabel)
        // Listener for JComboBox
        if (comboBoxQueryCategories.itemCount > 0) {
            comboBoxQueryCategories.selectedIndex = 0
        }
        //checkComboBoxQueryTypes(0, onConflictPanel)
        queryType = QueryType.TEMPLATE
        queryLabel.isVisible = false
        queryArea.isVisible = false
        scrollPane.isVisible = false
        onConflictPanel.isVisible = true

        //panel.preferredSize = Dimension(300, 50)
        //pack()
        comboBoxQueryCategories.addActionListener {
            val selectedItem = comboBoxQueryCategories.selectedItem
            if (selectedItem != null) {
                checkComboBoxQueryTypes(selectedItem, onConflictPanel, columnPanel)
            }
            checkConditions()
        }
        comboBoxPanel.add(comboBoxQueryCategories)


        val queryPanel = JPanel()
        queryPanel.layout = BoxLayout(queryPanel, BoxLayout.Y_AXIS)

        queryLabel.isVisible = false
        queryArea.isVisible = false
        queryArea.lineWrap = true // Перенесення рядків
        queryArea.wrapStyleWord = true // Переносить за словами
        queryArea.border = BorderFactory.createLineBorder(JBColor.GRAY) // Рамка навколо тексту

        queryPanel.add(queryLabel)
        queryPanel.add(Box.createVerticalStrut(5))
        queryPanel.add(scrollPane)

        isOKActionEnabled = false

        panel.add(namePanel)
        panel.add(comboBoxPanel)
        panel.add(columnPanel)
        panel.add(onConflictPanel)
        panel.add(queryPanel)
    }

    private fun checkComboBoxQueryTypes(selectedItem: Any, onConflictPanel: JPanel, columnPanel: JPanel) {
        when (selectedItem.toString()) {
            "Query" -> {
                queryType = QueryType.MANUAL
                scrollPane.isVisible = true
                queryLabel.isVisible = true
                queryArea.isVisible = true
                onConflictPanel.isVisible = false
                columnPanel.isVisible = false

                panel.preferredSize = Dimension(300, 200)
                pack()
            }

            "Insert" -> {
                queryType = QueryType.TEMPLATE
                queryLabel.isVisible = false
                queryArea.isVisible = false
                scrollPane.isVisible = false
                onConflictPanel.isVisible = true
                columnPanel.isVisible = false

                panel.preferredSize = Dimension(200, 100)
                pack()
            }

            "Update" -> {
                queryType = QueryType.TEMPLATE
                queryLabel.isVisible = false
                queryArea.isVisible = false
                scrollPane.isVisible = false
                onConflictPanel.isVisible = true
                columnPanel.isVisible = false

                panel.preferredSize = Dimension(200, 100)
                pack()
            }

            "Delete" -> {
                queryType = QueryType.TEMPLATE
                queryLabel.isVisible = false
                queryArea.isVisible = false
                scrollPane.isVisible = false
                onConflictPanel.isVisible = false
                columnPanel.isVisible = false

                panel.preferredSize = Dimension(200, 100)
                pack()
            }

            "SearchByLetter" -> {
                queryType = QueryType.TEMPLATE
                queryLabel.isVisible = false
                queryArea.isVisible = false
                scrollPane.isVisible = false
                onConflictPanel.isVisible = false
                columnPanel.isVisible = true

                comboBoxQueryColumn.removeAllItems()
                // Додаємо нові елементи в JComboBox
                val newItems = findConstructorParametersWithColumnInfo(ktFile)
                newItems.forEach { item ->
                    comboBoxQueryColumn.addItem(item)
                }
                // Зберігаємо новий список у listColumn
                listColumn = newItems

                columnSelect = comboBoxQueryColumn.selectedItem!!.toString()

                panel.preferredSize = Dimension(200, 100)
                pack()
            }

            else -> {
                queryType = QueryType.TEMPLATE
                queryLabel.isVisible = false
                queryArea.isVisible = false
                scrollPane.isVisible = false
                onConflictPanel.isVisible = false
                columnPanel.isVisible = false

                panel.preferredSize = Dimension(200, 50)
                pack()
            }
        }
    }

    private fun checkConditions() {
        val name = nameField.text
        // Condition check
        val conditionsMet: Boolean = comboBoxQueryOnConflict.isEnabled && name.isNotEmpty()

        // Activate or deactivate the OK button
        isOKActionEnabled = conditionsMet
    }

    private fun findConstructorParametersWithColumnInfo(ktFile: KtFile?): Array<String> {
        val fields = mutableListOf<String>()

        ktFile?.declarations?.forEach { declaration ->
            // Перевіряємо, чи це клас
            if (declaration is KtClass) {
                // Отримуємо primary constructor (первинний конструктор)
                val primaryConstructor = declaration.primaryConstructor

                // Перевіряємо, чи конструктор існує
                primaryConstructor?.valueParameters?.forEach { parameter ->
                    // Отримуємо тип параметра
                    val typeReference = parameter.typeReference?.text

                    // Якщо тип параметра String або String?, перевіряємо його анотації
                    if (typeReference == "String" || typeReference == "String?") {
                        var parameterNameToAdd: String? = null

                        // Перевіряємо анотації параметра
                        parameter.annotationEntries.forEach { annotation ->
                            val annotationName = annotation.shortName?.asString()
                            if (annotationName == "ColumnInfo") {
                                // Якщо знайдена анотація @ColumnInfo, перевіряємо атрибут `name`
                                annotation.valueArguments.forEach { argument ->
                                    val argumentName = argument.getArgumentName()?.asName?.asString()
                                    if (argumentName == "name") {
                                        // Якщо знайдений атрибут `name`, використовуємо його значення
                                        parameterNameToAdd =
                                            argument.getArgumentExpression()?.text?.removeSurrounding("\"")
                                    }
                                }
                            }
                        }

                        // Якщо анотація є, але параметр `name` відсутній
                        if (parameterNameToAdd == null) {
                            parameterNameToAdd = parameter.name // Використовуємо ім'я параметра
                        }

                        // Додаємо до списку знайдене значення
                        parameterNameToAdd?.let { fields.add(it) }
                    }
                }
            }
        }

        return fields.toTypedArray()
    }


    override fun doOKAction() {
        val name = nameField.text.trim()

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Name cannot be empty!", "Validation Error", JOptionPane.ERROR_MESSAGE)
            return
        }

        // Перевіряємо, чи список порожній
        if (listQuery.model.size > 0) {
            // Отримуємо всі імена зі списку
            val existingNames = (0 until listQuery.model.size).map { index ->
                listQuery.model.getElementAt(index).name
            }

            // Перевіряємо, чи є таке ім'я у списку
            if (existingNames.contains(name)) {
                JOptionPane.showMessageDialog(
                    null,
                    "Name already exists in the query list!",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
                )
                return
            }
        }

        super.doOKAction() // Продовжуємо закривати діалог
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

    fun getQueryCategory(): String {
        return comboBoxQueryCategories.selectedItem!!.toString()
    }

    fun getQueryType(): QueryType {
        return queryType
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

    fun getColumnSelected(): String? {
        return columnSelect
    }
}
