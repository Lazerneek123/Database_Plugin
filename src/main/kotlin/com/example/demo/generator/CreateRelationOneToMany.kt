package com.example.demo.generator

import com.example.demo.element.CapitalizeFirstLetter

class CreateRelationOneToMany {
    private lateinit var code: String
    private lateinit var nameClass: String

    fun getNameClass(): String {
        return nameClass
    }

    fun generate(
        path: String,
        packagePath1: String,
        packagePath2: String,
        className1: String,
        className2: String,
        tableName1: String,
        tableName2: String,
        parentColumn: String,
        entityColumn: String
    ): String {
        nameClass = "${CapitalizeFirstLetter().uppercaseChar(tableName1)}With${
            CapitalizeFirstLetter().uppercaseChar(tableName2)
        }"

        code = """
                package $path

                import androidx.room.Embedded
                import androidx.room.Relation${checkPackage(packagePath1)}${checkPackage(packagePath2)}

                data class ${nameClass}(
                ${generateColumns(className2, className1, parentColumn, entityColumn)}
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
        className2: String,
        className1: String,
        parentColumn: String,
        entityColumn: String
    ): String {
        return """    @Embedded val ${CapitalizeFirstLetter().lowercaseChar(className1)}: $className1,
                    @Relation(
                        parentColumn = "$parentColumn",
                        entityColumn = "$entityColumn"
                    )
                    val ${CapitalizeFirstLetter().lowercaseChar(className2)}s: List<$className2>"""
    }

    private fun columnQuestionMark(b: Boolean): String {
        return if (b) "?" else ""
    }
}