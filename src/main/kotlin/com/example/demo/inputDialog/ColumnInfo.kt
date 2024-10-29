package com.example.demo.inputDialog

import com.example.demo.model.ColumnAdvancedSettings
import com.example.demo.model.ForeignKey
import com.example.demo.tableConfig.ColumnAttribute
import com.intellij.openapi.ui.DialogWrapper
import org.jetbrains.kotlin.backend.common.descriptors.synthesizedName
import java.awt.BorderLayout
import javax.swing.*

class ColumnInfo(private val selectedElement: ColumnAdvancedSettings) : DialogWrapper(true) {
    private val panel = JPanel()

    private val columnAttributeListModel = DefaultListModel<Map.Entry<String?, String?>>()
    private val columnAttributeList = JList(columnAttributeListModel)

    private val attributeColumn = ColumnAttribute().get()

    init {
        title = "Column Info Attribute"
        init()

        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

        columnAttributeListModel.addElement(mapOf(attributeColumn[0] to selectedElement.name).entries.first())
        columnAttributeListModel.addElement(mapOf(attributeColumn[1] to selectedElement.typeAffinity).entries.first())
        columnAttributeListModel.addElement(mapOf(attributeColumn[2] to selectedElement.index).entries.first())
        columnAttributeListModel.addElement(mapOf(attributeColumn[3] to selectedElement.defaultValue).entries.first())
        columnAttributeListModel.addElement(mapOf(attributeColumn[4] to selectedElement.collate).entries.first())

        var selectedElement = ""

        panel.add(Box.createVerticalStrut(5))
        val panelPrimaryKey = JPanel()
        panelPrimaryKey.layout = BoxLayout(panelPrimaryKey, BoxLayout.Y_AXIS)
        panel.add(panelPrimaryKey)
        panel.add(Box.createVerticalStrut(5))

        columnAttributeList.addListSelectionListener { event ->
            if (!event.valueIsAdjusting) {
                val element = columnAttributeList.selectedValue
                if (element != null) {
                    selectedElement = element.key!!
                    val inputDialog = ColumnAttribute(selectedElement)
                    inputDialog.show()

                    // Get the results when click the OK button
                    if (inputDialog.isOK) {
                        val pair = inputDialog.getSelectedValue()
                        columnAttributeListModel.elements().toList().forEachIndexed { index, element ->
                            if (element.key == pair.key) {
                                val updatedElement = mapOf(element.key to pair.value).entries.first()
                                columnAttributeListModel.set(index, updatedElement)
                                isOKActionEnabled = true
                            }
                        }
                    }
                }
            }
        }

        // Adding a list to a panel
        panel.add(JScrollPane(columnAttributeList), BorderLayout.CENTER)

        // Adding buttons to a panel
        val buttonPanel = JPanel()
        panel.add(buttonPanel, BorderLayout.SOUTH)

        isOKActionEnabled = false

        panel.add(JScrollPane(columnAttributeList), BorderLayout.CENTER)
    }

    private val initialValues = DefaultListModel<Map.Entry<String?, String?>>()

    // A function to check if the values in the model have changed
    fun hasModelChanged(): Boolean {
        initialValues.addElement(mapOf(attributeColumn[0] to selectedElement.name).entries.first())
        initialValues.addElement(mapOf(attributeColumn[1] to null).entries.first())
        initialValues.addElement(mapOf(attributeColumn[2] to null).entries.first())
        initialValues.addElement(mapOf(attributeColumn[3] to null).entries.first())
        initialValues.addElement(mapOf(attributeColumn[4] to null).entries.first())

        columnAttributeListModel.elements().toList().forEachIndexed { index, element ->
            val currentValue = element.value
            val initialValue = initialValues[index].value

            if (initialValue != currentValue) {
                return true
            }
        }
        return false
    }

    fun getSelectedValue(): ColumnAdvancedSettings {
        with(selectedElement) {
            name = columnAttributeListModel.elements().toList()[0].value
            typeAffinity = columnAttributeListModel.elements().toList()[1].value
            index = columnAttributeListModel.elements().toList()[2].value
            defaultValue = columnAttributeListModel.elements().toList()[3].value
            collate = columnAttributeListModel.elements().toList()[4].value
        }
        return selectedElement
    }

    override fun createCenterPanel(): JComponent {
        return panel
    }
}