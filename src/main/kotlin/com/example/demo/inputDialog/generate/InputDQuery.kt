package com.example.demo.inputDialog.generate

import com.example.demo.element.TextFieldRegex
import com.example.demo.model.Query
import com.example.demo.model.QueryType
import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.ui.JBColor
import com.intellij.util.IconUtil
import org.jetbrains.kotlin.idea.core.util.toVirtualFile
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.GridBagLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.File
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class InputDQuery(
    private val listQuery: JList<Query>,
    private val directoryPath: String,
    private val project: Project
) : DialogWrapper(true) {
    private val panel = JPanel()

    private var selectedPath: String? = null
    private var selectedFileName: String? = null
    private var selectedClassName: String? = null
    private var selectedFilePathPackage: String? = null
    private var ktFile: KtFile? = null

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

        val chooseFilePane = JPanel(/*FlowLayout(FlowLayout.LEFT)*//*GridBagLayout()*/)
        chooseFilePane.layout = BoxLayout(chooseFilePane, BoxLayout.Y_AXIS)
        val icon = AllIcons.General.OpenDisk

        // Change the size of the icon
        val scaledIcon = IconUtil.scale(icon, 5.0)

        // Create a label for the image
        val labelIcon = JLabel(scaledIcon)
        val labelFile = JLabel("File Name:")
        labelIcon.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                fileChooser(labelFile)
            }
        })

        nameField.isEnabled = false
        comboBoxQueryCategories.isEnabled = false
        comboBoxQueryOnConflict.isEnabled = false

        chooseFilePane.add(labelIcon)
        chooseFilePane.add(labelFile)


        isOKActionEnabled = false

        panel.add(chooseFilePane)
        panel.add(namePanel)
        panel.add(comboBoxPanel)
        panel.add(columnPanel)
        panel.add(onConflictPanel)
        panel.add(queryPanel)
    }

    private fun fileChooser(labelFile: JLabel) {
        val currentDirectory = File(directoryPath)

        val fileChooser = JFileChooser(currentDirectory)
        fileChooser.fileSelectionMode = JFileChooser.FILES_ONLY // Limit file selection to files only

        val result = fileChooser.showOpenDialog(null) // Show file selection dialog

        if (result == JFileChooser.APPROVE_OPTION) {
            selectedPath = fileChooser.selectedFile.path

            val virtualFile = LocalFileSystem.getInstance().findFileByPath(selectedPath!!)!!
            val psiFile = PsiManager.getInstance(project).findFile(virtualFile)
            ktFile = psiFile as? KtFile

            // Checking for the presence of an annotation @Entity
            if (fileChooser.selectedFile.toVirtualFile() != null) {
                val isEntity = isEntityFile(fileChooser.selectedFile.toVirtualFile()!!, project)
                if (isEntity) {
                    selectedFileName = fileChooser.selectedFile.nameWithoutExtension

                    val selectedFile = fileChooser.selectedFile
                    val virtualFile = selectedFile.toVirtualFile()!!

                    // Якщо потрібно дізнатися назву класу в файлі:
                    val psiFile = PsiManager.getInstance(project).findFile(virtualFile) as? KtFile
                    selectedClassName = psiFile?.let { findClassNameInFile(it) } ?: selectedFile.name

                    // Getting the file package path
                    val sourceRoot = "src${File.separator}main${File.separator}java${File.separator}"
                    val packageName =
                        fileChooser.selectedFile.canonicalFile.parentFile.absolutePath.substringAfterLast(sourceRoot)
                    selectedFilePathPackage = packageName.replace(File.separator, ".") + ".$selectedClassName"

                    labelFile.text = "File Name: " + fileChooser.selectedFile.name

                    checkConditionsFileChoose()
                } else {
                    Messages.showErrorDialog(
                        "The file is not a table!",
                        "Error:"
                    )
                }
            } else {
                return
            }
        }
    }

    private fun findClassNameInFile(ktFile: KtFile): String? {
        return ktFile.declarations
            .filterIsInstance<KtClass>() // Знаходимо всі класи
            .firstOrNull()?.name         // Беремо назву першого класу
    }

    private fun checkConditionsFileChoose() {
        nameField.isEnabled = true
        comboBoxQueryCategories.isEnabled = true
        comboBoxQueryOnConflict.isEnabled = true
    }

    private fun isEntityFile(file: VirtualFile, project: Project): Boolean {
        // Завантажуємо файл як KtFile через PSI
        val psiFile = PsiManager.getInstance(project).findFile(file) as? KtFile ?: return false

        // Обходимо всі декларації у файлі
        for (declaration in psiFile.declarations) {
            // Перевіряємо, чи є це клас або об'єкт
            if (declaration is KtClassOrObject) {
                // Отримуємо всі анотації, прив'язані до класу/об'єкта
                val annotations = declaration.annotationEntries
                for (annotation in annotations) {
                    val annotationName = annotation.shortName?.asString()
                    if (annotationName == "Entity") {
                        return true
                    }
                }
            }
        }
        return false
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

                //columnSelect = comboBoxQueryColumn.selectedItem?.toString()

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
        val conditionsMet: Boolean =
            comboBoxQueryOnConflict.isEnabled && name.isNotEmpty() && selectedFileName!!.isNotEmpty()

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

    fun getSelectedFilePathPackage(): String {
        return selectedFilePathPackage.toString()
    }

    fun getSelectedFileName(): String {
        return selectedFileName.toString()
    }

    fun getSelectedClassName(): String {
        return selectedClassName.toString()
    }

    fun getKtFileSelecte(): KtFile {
        return ktFile!!
    }
}
