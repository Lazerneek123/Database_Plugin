package com.example.demo.input

import com.intellij.icons.AllIcons
import com.intellij.openapi.ui.DialogWrapper
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import com.intellij.util.IconUtil
import com.intellij.util.ui.JBUI
import java.awt.*
import javax.swing.*

class InputDialogChooseRelation : DialogWrapper(true) {
    private val panel = JPanel()

    private val nameLabel = JLabel("Choose Relation")

    init {
        title = "Creating A Relationship Between Tables"
        init()

        panelRelationTable()
        isOKActionEnabled = true
    }

    private fun panelRelationTable() {
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

        val relationTablePanel = JPanel()

        // Create a GridBagConstraints object to set the alignment
        val constraints = GridBagConstraints()
        constraints.gridx = 0
        constraints.gridy = 0
        constraints.fill = GridBagConstraints.CENTER
        constraints.insets = JBUI.insets(1) // Відступи

        // Uploading an image
        val icon = AllIcons.General.Tree

        // Change the size of the icon
        val scaledIcon = IconUtil.scale(icon, 5.0)

        val relationPanelOneToOne = JPanel(GridBagLayout())
        // Create a label for the image
        val labelRelationIcon1 = JLabel(scaledIcon)
        labelRelationIcon1.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                // Click your own InputDialog
                val inputDialogRelation = InputDialogRelation()
                inputDialogRelation.show()

                // Get the results when you click the OK button
                if (inputDialogRelation.isOK) {

                }
            }
        })
        val labelRelation1 = JLabel("1 : 1")
        relationPanelOneToOne.add(labelRelationIcon1, constraints)
        constraints.gridy++
        relationPanelOneToOne.add(labelRelation1, constraints)
        constraints.gridy--

        val relationPanelOneToMany = JPanel(GridBagLayout())
        val labelRelationIcon2 = JLabel(scaledIcon)
        labelRelationIcon2.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {

            }
        })
        val labelRelation2 = JLabel("1 : M")
        relationPanelOneToMany.add(labelRelationIcon2, constraints)
        constraints.gridy++
        relationPanelOneToMany.add(labelRelation2, constraints)
        constraints.gridy--

        val relationPanelManyToMany = JPanel(GridBagLayout())
        val labelRelationIcon3 = JLabel(scaledIcon)
        labelRelationIcon3.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {

            }
        })
        val labelRelation3 = JLabel("M : M")
        relationPanelManyToMany.add(labelRelationIcon3, constraints)
        constraints.gridy++
        relationPanelManyToMany.add(labelRelation3, constraints)
        constraints.gridy--

        val nameTablePanel = JPanel()
        nameTablePanel.layout = BoxLayout(nameTablePanel, BoxLayout.X_AXIS)

        relationTablePanel.add(nameLabel)
        nameTablePanel.add(relationPanelOneToOne)
        nameTablePanel.add(relationPanelOneToMany)
        nameTablePanel.add(relationPanelManyToMany)
        panel.add(relationTablePanel)
        panel.add(nameTablePanel)
    }

    override fun createCenterPanel(): JComponent {
        return panel
    }
}