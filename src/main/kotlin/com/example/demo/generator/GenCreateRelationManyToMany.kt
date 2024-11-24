package com.example.demo.generator

import com.example.demo.element.CapitalizeFirstLetter

class GenCreateRelationManyToMany {
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
        crossRefName: String,
        nameClass: String,
        parentColumn: String,
        entityColumn: String
    ): String {
        this.nameClass = nameClass

        code = """
                package $path

                import androidx.room.Embedded
                import androidx.room.Junction
                import androidx.room.Relation${checkPackage(packagePath1)}${checkPackage(packagePath2)}

                data class ${nameClass}(
                ${generateColumns(className2, className1, crossRefName, parentColumn, entityColumn)}
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
        crossRefName: String,
        parentColumn: String,
        entityColumn: String
    ): String {
        return """    @Embedded val ${CapitalizeFirstLetter().lowercaseChar(className1)}: $className1,
                    @Relation(
                        parentColumn = "$parentColumn",
                        entityColumn = "$entityColumn",
                        associateBy = Junction($crossRefName::class)
                    )
                    val ${CapitalizeFirstLetter().lowercaseChar(className2)}s: List<$className2>"""
    }

    private fun columnQuestionMark(b: Boolean): String {
        return if (b) "?" else ""
    }
}