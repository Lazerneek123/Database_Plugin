package com.example.demo.input_dialog

import javax.swing.JTextField
import javax.swing.text.AttributeSet
import javax.swing.text.PlainDocument

class TextFieldValue : JTextField() {
    private var selectedItem: String? = null

    private fun updateTextFieldRestrictions() {
        // Set the limit for the text field according to the user's choice
        when (selectedItem) {
            "String" -> setTextFieldToLatinCharactersOnly()
            "Int" -> setTextFieldToIntegerOnly()
            "Double" -> setTextFieldToNumeric(true)
            "Long" -> setTextFieldToNumeric(false)
            //"Boolean" -> setBoolean(boolean)
            "Float", "Byte" -> setTextFieldToFloatOrByteOnly()
            // Інші варіанти вибору
            else -> resetTextFieldRestrictions()
        }
    }

    private fun setTextFieldToLatinCharactersOnly() {
        document = createDocumentForRegex("[a-zA-Z]*")
    }

    private fun setTextFieldToIntegerOnly() {
        document = createDocumentForRegex("\\d*")
    }

    private fun setTextFieldToNumeric(allowDecimal: Boolean) {
        val regex = if (allowDecimal) "-?\\d*(\\.\\d*)?" else "-?\\d*"
        document = createDocumentForRegex(regex)
    }

    private fun setTextFieldToFloatOrByteOnly() {
        document = createDocumentForRegex("-?\\d*(\\.\\d*)?")
    }

    private fun resetTextFieldRestrictions() {
        document = PlainDocument()
    }

    private fun createDocumentForRegex(regex: String): PlainDocument {
        return object : PlainDocument() {
            override fun insertString(offset: Int, str: String?, attr: AttributeSet?) {
                if (str == null) return
                if (str.matches(Regex(regex))) {
                    super.insertString(offset, str, attr)
                }
            }
        }
    }

    fun setSelectedItem(selectedItem: String?) {
        this.selectedItem = selectedItem
        updateTextFieldRestrictions()
    }
}