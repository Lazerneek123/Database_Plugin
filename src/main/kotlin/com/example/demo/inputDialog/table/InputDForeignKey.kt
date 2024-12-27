package com.example.demo.inputDialog.table

import com.example.demo.model.Column
import com.example.demo.model.PrimaryKey
import com.intellij.openapi.ui.DialogWrapper
import com.example.demo.model.ForeignKey
import com.example.demo.tableConfig.ForeignKeyAttribute
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import java.awt.BorderLayout
import javax.swing.*
import javax.swing.event.ListDataEvent
import javax.swing.event.ListDataListener

class InputDForeignKey(
    listModelPrimaryKey: DefaultListModel<PrimaryKey>,
    listModelColumn: DefaultListModel<Column>,
    directoryPath: String,
    project: Project
) : DialogWrapper(true) {
    private val panel = JPanel()

    private val foreignKeyAttributeListModel = DefaultListModel<Map.Entry<String?, Any?>>()
    private val foreignKeyAttributeList = JList(foreignKeyAttributeListModel)

    private var parentColumnsList = DefaultListModel<String>()

    private var foreignKey: ForeignKey? = null
    fun getForeignKey(): ForeignKey {
        return foreignKey!!
    }

    private val attributeForeignKey = ForeignKeyAttribute().get()

    init {
        title = "Foreign Key Attribute"
        init()

        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)


        var selectedElement = ""

        attributeForeignKey.forEach { attributeName -> foreignKeyAttributeListModel.addElement(mapOf(attributeName to null).entries.first()) }

        panel.add(Box.createVerticalStrut(5))
        val panelPrimaryKey = JPanel()
        panelPrimaryKey.layout = BoxLayout(panelPrimaryKey, BoxLayout.Y_AXIS)
        panel.add(panelPrimaryKey)
        panel.add(Box.createVerticalStrut(5))

        // Add a change listener to the list model
        foreignKeyAttributeListModel.addListDataListener(object : ListDataListener {
            override fun intervalAdded(e: ListDataEvent) {
                isOKActionEnabled = true
            }

            override fun intervalRemoved(e: ListDataEvent) {
                isOKActionEnabled = false
            }

            override fun contentsChanged(e: ListDataEvent) {

            }
        })
        foreignKeyAttributeList.addListSelectionListener { event ->
            if (!event.valueIsAdjusting) {
                val element = foreignKeyAttributeList.selectedValue
                if (element != null) {
                    selectedElement = element.key!!
                    val inputDialog =
                        InputDForeignKeyAttribute(
                            selectedElement,
                            parentColumnsList,
                            listModelPrimaryKey,
                            listModelColumn,
                            directoryPath
                        )
                    inputDialog.show()

                    // Get the results when you click the OK button
                    if (inputDialog.isOK) {
                        if (selectedElement == attributeForeignKey[1]) {
                            parentColumnsList = inputDialog.getParentColumnsListModel()
                        }

                        val pair = inputDialog.getSelectedValue()
                        foreignKeyAttributeListModel.elements().toList().forEachIndexed { index, element ->
                            if (element.key == pair.key) {
                                val updatedElement = mapOf(element.key to pair.value).entries.first()
                                foreignKeyAttributeListModel.set(index, updatedElement)
                                //project.showNotification("key " + updatedElement.key!! + " index:$index value " + updatedElement.value)
                            }
                        }
                        foreignKey = ForeignKey(
                            foreignKeyAttributeListModel.elements().toList()[0].value as String?,
                            foreignKeyAttributeListModel.elements().toList()[1].value as List<String?>?,
                            foreignKeyAttributeListModel.elements().toList()[2].value as List<String?>?,
                            foreignKeyAttributeListModel.elements().toList()[3].value as String?,
                            foreignKeyAttributeListModel.elements().toList()[4].value as String?,
                            foreignKeyAttributeListModel.elements().toList()[5].value as String?
                        )
                    }
                }
            }
        }

        panel.add(JScrollPane(foreignKeyAttributeList), BorderLayout.CENTER)
    }

    private fun Project.showNotification(message: String) {
        val groupId = "Database Plugin Notification Group"
        NotificationGroupManager.getInstance().getNotificationGroup(groupId)
            .createNotification("Database class", message, NotificationType.INFORMATION).notify(this)
    }

    override fun createCenterPanel(): JComponent {
        return panel
    }
}