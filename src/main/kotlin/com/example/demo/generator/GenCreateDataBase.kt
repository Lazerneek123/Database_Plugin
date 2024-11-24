package com.example.demo.generator

import com.example.demo.element.CapitalizeFirstLetter
import com.example.demo.model.Column
import com.example.demo.model.PrimaryKey
import com.example.demo.tableConfig.TableCreate

class GenCreateDataBase {
    private var code = ""

    fun generate(
        config: TableCreate
    ): String {
        code = """
                package ${config.getPath()}

                import androidx.room.Entity
                import androidx.room.PrimaryKey

                @Entity
                data class ${CapitalizeFirstLetter().uppercaseChar(config.getTableName())}(
                ${generateColumns(config.getListColumnData())}
                ) {
                    ${generatePrimaryKeys(config.getListPrimaryKeyData())}       
                }
            """.trimIndent()

        return code
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
            val cont = """
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
}