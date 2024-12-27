package com.example.demo.inputDialog.table

import com.example.demo.model.Column
import com.example.demo.model.PrimaryKey
import com.example.demo.tableConfig.ForeignKeyAttribute
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import java.awt.CardLayout
import java.io.File
import javax.swing.*

class InputDForeignKeyAttribute(
    selectedElement: String,
    parentColumns: DefaultListModel<String>,
    listModelPrimaryKey: DefaultListModel<PrimaryKey>,
    listModelColumn: DefaultListModel<Column>,
    private var directoryPath: String
) : DialogWrapper(true) {
    private val panel = JPanel()

    private val panelMain = JPanel(CardLayout())
    private var selectedFileName: String? = null


    private val attributeForeignKey = ForeignKeyAttribute().get()

    private var currentCardName: String = selectedElement

    // Panel for entity
    private val entityPanel = JPanel().apply {
        val addBtnEntity = JButton("Choose file")
        addBtnEntity.addActionListener {
            fileChooser(addBtnEntity)
        }
        add(addBtnEntity)
    }

    // Panel for parent columns
    private val parentColumnsListModel = parentColumns
    private val parentColumnsList = JList(parentColumnsListModel)
    private val parentColumnsPanel = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)

        parentColumnsList.addListSelectionListener {
            isOKActionEnabled = parentColumnsListModel.size() > 0
        }

        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.X_AXIS)
        add(JScrollPane(parentColumnsList))
        add(panel)
    }

    // Panel for child columns
    private val childColumnsListModel = DefaultListModel<String>()
    private val childColumnsList = JList(childColumnsListModel)
    private val childColumnsPanel = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)

        // Add a list of all rows
        listModelPrimaryKey.elements().toList().forEach { element ->
            childColumnsListModel.addElement(element.name)
        }
        listModelColumn.elements().toList().forEach { element ->
            childColumnsListModel.addElement(element.name)
        }

        childColumnsList.addListSelectionListener {
            isOKActionEnabled = childColumnsListModel.size() > 0
        }

        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.X_AXIS)
        add(JScrollPane(childColumnsList))
        add(panel)
    }

    // Panel for on delete
    private val comboBoxOnDelete = JComboBox(ForeignKeyAttribute().getAction())
    private val onDeletePanel = JPanel().apply {
        comboBoxOnDelete.addActionListener {
            isOKActionEnabled = comboBoxOnDelete.selectedItem != null
        }
        add(comboBoxOnDelete)
    }

    // Panel for on update
    private val comboBoxOnUpdate = JComboBox(ForeignKeyAttribute().getAction())
    private val onUpdatePanel = JPanel().apply {
        comboBoxOnUpdate.addActionListener {
            isOKActionEnabled = comboBoxOnUpdate.selectedItem != null
        }
        add(comboBoxOnUpdate)
    }

    // Panel for deferred
    private val comboBoxDeferred = JComboBox(arrayOf("true", "false"))
    private val deferredPanel = JPanel().apply {
        comboBoxDeferred.addActionListener {
            isOKActionEnabled = comboBoxDeferred.selectedItem != null
        }
        add(comboBoxDeferred)
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

        /*if (currentCardName == attributeForeignKey[0]) {
            fileChooser()
        }*/
        panelMain.add(entityPanel, attributeForeignKey[0])
        panelMain.add(parentColumnsPanel, attributeForeignKey[1])
        panelMain.add(childColumnsPanel, attributeForeignKey[2])
        panelMain.add(onDeletePanel, attributeForeignKey[3])
        panelMain.add(onUpdatePanel, attributeForeignKey[4])
        panelMain.add(deferredPanel, attributeForeignKey[5])


        val cl = panelMain.layout as CardLayout
        cl.show(panelMain, currentCardName)


        // Основна панель
        val mainPanel = JPanel()
        mainPanel.layout = BoxLayout(mainPanel, BoxLayout.Y_AXIS)
        mainPanel.add(panelMain)

        panel.add(mainPanel)

        //updateValueField()
        panel.add(valuePanel)
    }

    private fun fileChooser(addBtnEntity: JButton) {
        val currentDirectory = File(directoryPath)

        val fileChooser = JFileChooser(currentDirectory)
        fileChooser.fileSelectionMode = JFileChooser.FILES_ONLY // Limit file selection to files only

        val result = fileChooser.showOpenDialog(null) // Show file selection dialog

        if (result == JFileChooser.APPROVE_OPTION) {
            val fileContent = fileChooser.selectedFile.readText()

            // Перевіряємо наявність анотації @Entity
            val entityPattern = Regex("@Entity\\s*\\(.*\\)")
            val isEntity = entityPattern.containsMatchIn(fileContent)

            if (isEntity) {
                selectedFileName = fileChooser.selectedFile.nameWithoutExtension
                addBtnEntity.text = fileChooser.selectedFile.name

                // Знаходимо всі оголошення полів (var або val)
                val fieldPattern = Regex("(var|val)\\s+(\\w+):\\s*([\\w?<>]+)")
                val matches = fieldPattern.findAll(fileContent)

                // Виводимо назви полів та їх типи
                matches.forEach { match ->
                    val type = match.groupValues[1] // var or val
                    val name = match.groupValues[2] // Name row
                    val dataType = match.groupValues[3] // Data type

                    parentColumnsListModel.addElement(name)
                }
                isOKActionEnabled = true
            } else {
                Messages.showErrorDialog(
                    "The file does not contain the @Entity annotation!",
                    "Error:"
                )
                isOKActionEnabled = false
            }
        }
    }

    fun getParentColumnsListModel(): DefaultListModel<String> {
        return parentColumnsListModel
    }

    override fun createCenterPanel(): JComponent {
        return panel
    }

    fun getSelectedValue(): Map.Entry<String, Any?> {
        return when (currentCardName) {
            attributeForeignKey[0] -> mapOf(currentCardName to selectedFileName!!).entries.first()
            attributeForeignKey[1] -> mapOf(currentCardName to parentColumnsList.selectedValuesList).entries.first()
            attributeForeignKey[2] -> mapOf(currentCardName to childColumnsList.selectedValuesList).entries.first()
            attributeForeignKey[3] -> mapOf(currentCardName to comboBoxOnDelete.selectedItem!!).entries.first()
            attributeForeignKey[4] -> mapOf(currentCardName to comboBoxOnUpdate.selectedItem!!).entries.first()
            attributeForeignKey[5] -> mapOf(currentCardName to comboBoxDeferred.selectedItem!!).entries.first()
            else -> mapOf("key" to "value").entries.first()
        }
    }
}
