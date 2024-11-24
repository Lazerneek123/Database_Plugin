package com.example.demo.inputDialog

import com.example.demo.model.Column
import com.example.demo.model.Index
import com.example.demo.model.PrimaryKey
import com.example.demo.tableConfig.IndexAttribute
import com.intellij.openapi.ui.DialogWrapper
import java.awt.BorderLayout
import javax.swing.*
import javax.swing.event.ListDataEvent
import javax.swing.event.ListDataListener

class InputDIndex(
    listModelPrimaryKey: DefaultListModel<PrimaryKey>,
    listModelColumn: DefaultListModel<Column>
) : DialogWrapper(true) {
    private val panel = JPanel()

    private val indexAttributeListModel = DefaultListModel<Map.Entry<String?, Any?>>()
    private val indexAttributeList = JList(indexAttributeListModel)

    private var countValue = 0

    private var index: Index? = null
    fun getIndex(): Index {
        return index!!
    }

    private val attributeIndex = IndexAttribute().get()

    init {
        title = "Index Attribute"
        init()

        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)


        var selectedElement = ""

        attributeIndex.forEach { attributeName -> indexAttributeListModel.addElement(mapOf(attributeName to null).entries.first()) }

        panel.add(Box.createVerticalStrut(5))
        val panelPrimaryKey = JPanel()
        panelPrimaryKey.layout = BoxLayout(panelPrimaryKey, BoxLayout.Y_AXIS)
        panel.add(panelPrimaryKey)
        panel.add(Box.createVerticalStrut(5))

        // Add a change listener to the list model
        indexAttributeListModel.addListDataListener(object : ListDataListener {
            override fun intervalAdded(e: ListDataEvent) {
                isOKActionEnabled = true
            }

            override fun intervalRemoved(e: ListDataEvent) {
                isOKActionEnabled = false
            }

            override fun contentsChanged(e: ListDataEvent) {

            }
        })
        indexAttributeList.addListSelectionListener { event ->
            if (!event.valueIsAdjusting) {
                val element = indexAttributeList.selectedValue
                if (element != null) {
                    selectedElement = element.key!!
                    val inputDIndexAttributeInputDialog =
                        InputDIndexAttribute(countValue, selectedElement, listModelPrimaryKey, listModelColumn)
                    inputDIndexAttributeInputDialog.show()

                    // Get the results when you click the OK button
                    if (inputDIndexAttributeInputDialog.isOK) {
                        if (selectedElement == attributeIndex[1]) {
                            countValue = inputDIndexAttributeInputDialog.getValueCount()
                        }

                        val pair = inputDIndexAttributeInputDialog.getSelectedValue()
                        indexAttributeListModel.elements().toList().forEachIndexed { index, element ->
                            if (element.key == pair.key) {
                                val updatedElement = mapOf(element.key to pair.value).entries.first()
                                indexAttributeListModel.set(index, updatedElement)
                            }
                        }
                        index = Index(
                            indexAttributeListModel.elements().toList()[0].value as String?,
                            indexAttributeListModel.elements().toList()[1].value as List<String?>?,
                            indexAttributeListModel.elements().toList()[2].value as List<String?>?,
                            indexAttributeListModel.elements().toList()[3].value as String?
                        )
                    }
                }
            }
        }

        panel.add(JScrollPane(indexAttributeList), BorderLayout.CENTER)
    }

    override fun createCenterPanel(): JComponent {
        return panel
    }
}