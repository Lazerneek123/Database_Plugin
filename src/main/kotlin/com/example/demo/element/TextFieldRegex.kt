package com.example.demo.element

import javax.swing.text.AttributeSet
import javax.swing.text.Document
import javax.swing.text.PlainDocument

class TextFieldRegex(document: Document) {
    private var document: Document

    init {
        this.document = document
    }

    fun setTextFieldToLatinCharactersOnly(): Document {
        document = createDocumentForRegex("[a-zA-Z]*")
        return document
    }

    fun setTextFieldToIntegerOnly(): Document {
        document = createDocumentForRegex("\\d*")
        return document
    }

    fun setTextFieldToNumeric(allowDecimal: Boolean): Document {
        val regex = if (allowDecimal) "-?\\d*(\\.\\d*)?" else "-?\\d*"
        document = createDocumentForRegex(regex)
        return document
    }

    fun setTextFieldToFloatOrByteOnly(): Document {
        document = createDocumentForRegex("-?\\d*(\\.\\d*)?")
        return document
    }

    fun resetTextFieldRestrictions(): Document {
        document = PlainDocument()
        return document
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
}