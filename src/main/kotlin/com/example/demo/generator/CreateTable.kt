package com.example.demo.generator

import com.example.demo.model.Column
import com.example.demo.model.PrimaryKey

class CreateTable {
    private var content = ""

    fun generate(
        path: String,
        tableName: String,
        listPrimaryKeyData: List<PrimaryKey>,
        listColumnData: List<Column>
    ): String {
        content = """
                package $path

                import androidx.room.ColumnInfo
                import androidx.room.Entity
                import androidx.room.PrimaryKey

                @Entity(tableName = "$tableName")
                data class ${capitalizeFirstLetter(tableName)}(
                ${generateColumns(listColumnData)}
                ) {
                    ${generatePrimaryKeys(listPrimaryKeyData)}       
                }
            """.trimIndent()

        return content
    }

    private fun generatePrimaryKeys(list: List<PrimaryKey>): String {
        var content = ""

        for (i in list.indices) {
            if (i >= 1) {
                content += """
                    
                """
            }
            val autoGenerate = list[i].autoGenerateValue
            val name = list[i].name
            val dataType = list[i].dataType
            val value = list[i].value

            val cont = """    
                    @PrimaryKey(autoGenerate = $autoGenerate)
                    @ColumnInfo(name = "$name")
                    var $name: $dataType = $value"""
            content += cont
        }
        return content
    }

    private fun generateColumns(list: List<Column>): String {
        var content = ""

        for (i in list.indices) {
            if (i >= 1) {
                content += """
                    
                """
            }
            val nullable = list[i].nullable
            val cont = """    @ColumnInfo(name = "${list[i].name}")
                    val ${list[i].name}: ${list[i].dataType}${columnQuestionMark(nullable)} = ${list[i].value}"""
            content += cont

            if (i < list.size - 1) {
                content += ","
            }
        }
        return content
    }

    private fun columnQuestionMark(b: Boolean): String {
        return if (b) "?" else ""
    }

    private fun capitalizeFirstLetter(str: String): String {
        if (str.isEmpty()) return str
        val firstChar = str[0].uppercaseChar()
        return firstChar + str.substring(1)
    }
}