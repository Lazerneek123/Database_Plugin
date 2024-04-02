package com.example.demo.input

import com.intellij.openapi.ui.DialogWrapper
import javax.swing.BoxLayout
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class InputDialogRelation : DialogWrapper(true) {
    private val panel = JPanel()

    private val nameLabel = JLabel("Choose Relation")


    init {
        title = "Relationship"
        init()

        panelRelationTable()
        isOKActionEnabled = true
    }

    private fun panelRelationTable() {
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

        val relationTablePanel = JPanel()
        relationTablePanel.layout = BoxLayout(relationTablePanel, BoxLayout.X_AXIS)




        val nameTablePanel = JPanel()
        nameTablePanel.layout = BoxLayout(nameTablePanel, BoxLayout.X_AXIS)

        relationTablePanel.add(nameLabel)

        panel.add(relationTablePanel)
        panel.add(nameTablePanel)
    }

    override fun createCenterPanel(): JComponent {
        return panel
    }
}