package com.example.demo.element

class CapitalizeFirstLetter {
    fun setString(str: String): String {
        if (str.isEmpty()) return str
        val firstChar = str[0].uppercaseChar()
        return firstChar + str.substring(1)
    }
}