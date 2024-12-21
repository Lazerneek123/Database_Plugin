package com.example.demo.inputDialog.generate

import com.example.demo.element.TextFieldRegex
import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.IconUtil
import com.intellij.util.ui.JBUI
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.File
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class InputDLiveData(
    private val directoryPath: String,
    private val caretOffset: Int,
    private val psiFile: PsiFile,
    private val project: Project
) : DialogWrapper(true) {

    private val panelMain = JPanel()
    private var selectedFileName: String? = null
    private var selectedPath: String? = null
    private var selectedFilePathPackage: String? = null

    private val nameLabel = JLabel("Name:")
    private val nameField = JTextField()

    private val viewModelLabel = JLabel("View Model")
    private val listModelNameField = DefaultListModel<String>()
    private val listNameField = JList(listModelNameField).apply {
        selectionMode = ListSelectionModel.SINGLE_SELECTION // Дозволяємо вибір лише одного елемента
    }

    private val typeLabel = JLabel("Type:")

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

    private val dataStructureLabel = JLabel("Data structure:")

    // Creating options for a drop-down list
    private val dataStructure = arrayOf(
        "List",
        "ArrayList",
        "Array",
        "Set",
        "Queue",
        "Stack",
        "Result",
        "Channel",
        "Optional",
        "Deferred"
    )

    // Creating a drop-down list
    private val comboBoxDateType = JComboBox(dataTypes)
    private val comboBoxDateStructure = JComboBox(dataStructure)
    private val listCheckBox = JCheckBox("Structure")

    init {
        title = "Generate LiveData"
        init()
        isOKActionEnabled = false

        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.X_AXIS)
        panel.add(nameLabel)
        addDocumentListenerTextField(nameField)
        panel.add(nameField)
        panelMain.add(panel)
        isOKActionEnabled = true

        panelType()
        panelChooseFile()

        panelMain.add(Box.createVerticalStrut(5))
        val panelColumn = JPanel()
        panelColumn.layout = BoxLayout(panelColumn, BoxLayout.X_AXIS)
        panelColumn.add(viewModelLabel)
        panelMain.add(panelColumn)
        panelMain.add(Box.createVerticalStrut(5))

        panelFields()
        isOKActionEnabled = false
    }

    private fun panelType() {
        val typePanel = JPanel()
        typePanel.layout = BoxLayout(typePanel, BoxLayout.X_AXIS)

        // Listener for JComboBox
        comboBoxDateType.addActionListener {
            //checkConditions()
        }

        listCheckBox.addChangeListener {
            comboBoxDateStructure.isEnabled = listCheckBox.isSelected
        }

        typePanel.add(typeLabel)
        typePanel.add(comboBoxDateType)
        typePanel.add(dataStructureLabel)
        typePanel.add(comboBoxDateStructure)
        comboBoxDateStructure.isEnabled = false
        typePanel.add(listCheckBox)
        panelMain.add(typePanel)
        panelMain.add(Box.createVerticalStrut(15))
    }

    private fun panelFields() {
        listNameField.addListSelectionListener { checkConditions() }
        panelMain.add(JScrollPane(listNameField), BorderLayout.CENTER)
    }

    private fun getListNameFields(caretOffset: Int, psiFile: PsiFile, nameClass: String): ArrayList<String> {
        val ktFile = psiFile as? KtFile

        val elementAtCaret = psiFile.findElementAt(caretOffset)

        // Пошук методу, в якому знаходиться курсор
        val containingFunction = PsiTreeUtil.getParentOfType(elementAtCaret, KtNamedFunction::class.java)

        // Отримуємо всі локальні змінні в методі
        val localVariables = containingFunction!!.bodyExpression?.children?.filterIsInstance<KtProperty>()

        val list: ArrayList<String> = ArrayList()

        val fields = getFieldsByType(ktFile!!, nameClass)
        fields.forEach {
            list.add(it.name!!)
        }

        localVariables?.filter {
            it.typeReference?.text == nameClass
        }?.forEach {
            list.add(it.name ?: "")
        }
        return list
    }

    private fun checkViewModelLabel(nameClass: String) {
        if (listModelNameField.size != 0) {
            viewModelLabel.text = "Select name view model | Type view model: $nameClass"
        } else {
            viewModelLabel.text = "Name view model"
        }
    }

    private fun getFieldsByType(psiFile: KtFile, fieldType: String): List<KtProperty> {
        val fields = mutableListOf<KtProperty>()

        // Отримуємо всі Kotlin класи в файлі
        val ktClasses = psiFile.getChildrenOfType<KtClass>()

        ktClasses.forEach { ktClass ->
            // Отримуємо всі властивості класу
            val ktProperties = ktClass.declarations.filterIsInstance<KtProperty>()

            // Фільтруємо властивості за типом
            ktProperties.forEach { ktProperty ->
                val propertyType = ktProperty.typeReference?.text
                if (propertyType == fieldType) {
                    fields.add(ktProperty)
                }
            }
        }
        return fields
    }

    private fun panelChooseFile() {
        panelMain.layout = BoxLayout(panelMain, BoxLayout.Y_AXIS)

        // Create a GridBagConstraints object to set the alignment
        val constraints = GridBagConstraints()
        constraints.gridx = 0
        constraints.gridy = 0
        constraints.fill = GridBagConstraints.CENTER
        constraints.insets = JBUI.insets(1)

        // Uploading an image
        val icon = AllIcons.General.OpenDisk

        // Change the size of the icon
        val scaledIcon = IconUtil.scale(icon, 5.0)

        val panel = JPanel(GridBagLayout())
        // Create a label for the image
        val labelIcon = JLabel(scaledIcon)
        val labelFile = JLabel("File name:")
        labelIcon.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                fileChooser(labelFile)
                checkConditions()
            }
        })
        panel.add(JLabel("Select the type of view model where the data will be created"))
        constraints.gridy++
        panel.add(labelIcon, constraints)
        constraints.gridy++
        panel.add(labelFile, constraints)
        constraints.gridy--


        val nameTablePanel = JPanel()
        nameTablePanel.layout = BoxLayout(nameTablePanel, BoxLayout.X_AXIS)

        nameTablePanel.add(panel)
        panelMain.add(nameTablePanel)
    }

    private fun fileChooser(labelFile: JLabel) {
        val currentDirectory = File(directoryPath)

        val fileChooser = JFileChooser(currentDirectory)
        fileChooser.fileSelectionMode = JFileChooser.FILES_ONLY // Limit file selection to files only

        val result = fileChooser.showOpenDialog(null) // Show file selection dialog

        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFileName = fileChooser.selectedFile.nameWithoutExtension
            selectedPath = fileChooser.selectedFile.path
            // Getting the file package path
            val sourceRoot = "src${File.separator}main${File.separator}java${File.separator}"
            val packageName =
                fileChooser.selectedFile.canonicalFile.parentFile.absolutePath.substringAfterLast(sourceRoot)
            selectedFilePathPackage = packageName.replace(File.separator, ".") + ".$selectedFileName"

            labelFile.text = "File Name: " + fileChooser.selectedFile.name
            //checkConditions()

            val filePath = getSelectePath() // Шлях до файлу у вигляді String
            val virtualFileSelected = LocalFileSystem.getInstance().findFileByPath(filePath)
            val psiFileSelected = PsiManager.getInstance(project).findFile(virtualFileSelected!!)
            var nameClass: String? = null
            if (psiFileSelected is KtFile) {
                // Знаходимо всі класи в цьому файлі
                val ktClasses = psiFileSelected.declarations.filterIsInstance<KtClass>()
                ktClasses.forEach { ktClass ->
                    nameClass = ktClass.name!!
                }
            }

            listModelNameField.clear()
            val listField = getListNameFields(caretOffset, psiFile, nameClass!!)
            listField.forEach { field ->
                listModelNameField.addElement(field)
            }
            checkViewModelLabel(nameClass!!)
        }
    }

    private fun addDocumentListenerTextField(textField: JTextField) {
        // Document listener for a text field
        textField.document.addDocumentListener(object : DocumentListener {
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

    private fun checkConditions() {
        val nameTable = nameField.text
        // Condition check
        isOKActionEnabled =
            nameTable.isNotEmpty() && (selectedFileName?.isNotEmpty() ?: false) && !listNameField.isSelectionEmpty
    }

    private fun setTextFieldToLatinCharactersOnly() {
        nameField.document = TextFieldRegex(nameField.document).setTextFieldToLatinCharactersOnly()
    }

    override fun createCenterPanel(): JComponent {
        return panelMain
    }

    fun getSelectePath(): String {
        return selectedPath.toString()
    }

    fun getSelecteViewModel(): String {
        return listNameField.selectedValue.toString()
    }

    fun getName(): String {
        return nameField.text
    }

    fun getType(): String {
        return comboBoxDateType.selectedItem!!.toString()
    }

    fun getStructureType(): String {
        return comboBoxDateStructure.selectedItem!!.toString()
    }

    fun getStructure(): Boolean {
        return listCheckBox.isSelected
    }
}