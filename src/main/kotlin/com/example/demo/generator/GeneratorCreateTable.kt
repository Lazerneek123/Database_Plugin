package com.example.demo.generator

import com.example.demo.model.ColumnData

class GeneratorCreateTable {
    private var content = ""

    fun generate(
        path: String,
        tableName: String,
        primaryKeyName: String,
        primaryKeyAutoGenerate: Boolean,
        primaryKeyValue: String,
        listColumnData: List<ColumnData>
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
                    @PrimaryKey(autoGenerate = ${primaryKeyAutoGenerate})
                    @ColumnInfo(name = "$primaryKeyName")
                    private var $primaryKeyName: Int = ${
            if (!primaryKeyAutoGenerate) {
                primaryKeyValue
            } else {
                0
            }
        }
        
                    fun get${capitalizeFirstLetter(primaryKeyName)}(): Int {
                        return $primaryKeyName
                    }
                }
            """.trimIndent()

        return content
    }

    private fun generateColumns(list: List<ColumnData>): String {
        var content = ""

        for (i in list.indices) {
            if (i >= 1) {
                content += """
                    
                """
            }
            val cont = """    @ColumnInfo(name = "${list[i].name}")
                    val ${list[i].name}: ${list[i].dataType} = ${list[i].value}"""
            content += cont

            if (i < list.size - 1) {
                content += ","
            }
        }
        return content
    }

    private fun capitalizeFirstLetter(str: String): String {
        if (str.isEmpty()) return str
        val firstChar = str[0].uppercaseChar()
        return firstChar + str.substring(1)
    }
}