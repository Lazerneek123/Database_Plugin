package com.example.demo.element

class CapitalizeFirstLetter {
    fun uppercaseChar(str: String): String {
        if (str.isEmpty()) return str
        val firstChar = str[0].uppercaseChar()
        return firstChar + str.substring(1)
    }

    fun lowercaseChar(str: String): String {
        if (str.isEmpty()) return str
        val firstChar = str[0].lowercaseChar()
        return firstChar + str.substring(1)
    }
}