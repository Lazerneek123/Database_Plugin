package com.example.demo.element

import javax.swing.JTextField

class TextFieldValuePrimaryKey : JTextField() {
    private var selectedItem: String? = null

    private fun updateTextFieldRestrictions() {
        val textFieldRegex = TextFieldRegex(document)
        // Set the limit for the text field according to the user's choice
        document = when (selectedItem) {
            "String" -> textFieldRegex.setTextFieldToLatinCharactersOnly()
            "Int" -> textFieldRegex.setTextFieldToIntegerOnly()
            "Long" -> textFieldRegex.setTextFieldToNumeric(false)
            "Byte" -> textFieldRegex.setTextFieldToFloatOrByteOnly()
            // Other options to choose from
            else -> textFieldRegex.resetTextFieldRestrictions()
        }
    }

    fun setSelectedItem(selectedItem: String?) {
        this.selectedItem = selectedItem
        updateTextFieldRestrictions()
    }
}