package com.example.demo.generator

import com.example.demo.element.CapitalizeFirstLetter

class CreateRelationOneToOne {
    private var code = ""

    fun generate(
        path: String,
        packagePath1: String,
        packagePath2: String,
        tableName1: String,
        tableName2: String,
        parentColumn: String,
        entityColumn: String
    ): String {
        code = """
                package $path

                import androidx.room.Embedded
                import androidx.room.Relation${checkPackage(packagePath1)}${checkPackage(packagePath2)}

                data class ${CapitalizeFirstLetter().uppercaseChar(tableName1)}And${
            CapitalizeFirstLetter().uppercaseChar(
                tableName2
            )
        }(
                ${generateColumns(tableName1, tableName2, parentColumn, entityColumn)}
                )""".trimIndent()

        return code
    }

    private fun checkPackage(packagePath: String): String {
        // Check to receive the path of the package
        return if (packagePath == "") {
            ""
        } else {
            "\n                import $packagePath"
        }
    }

    private fun generateColumns(
        tableName1: String,
        tableName2: String,
        parentColumn: String,
        entityColumn: String
    ): String {
        return """    @Embedded val ${CapitalizeFirstLetter().lowercaseChar(tableName2)}: ${
            CapitalizeFirstLetter().uppercaseChar(tableName2)
        },
                    @Relation(
                        parentColumn = "$parentColumn",
                        entityColumn = "$entityColumn"
                    )
                    val ${CapitalizeFirstLetter().lowercaseChar(tableName1)}: ${
            CapitalizeFirstLetter().uppercaseChar(
                tableName1
            )
        }"""
    }

    private fun columnQuestionMark(b: Boolean): String {
        return if (b) "?" else ""
    }
}