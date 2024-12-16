package com.example.demo.generator

import com.example.demo.dAOConfig.DAOCreate
import com.example.demo.element.CapitalizeFirstLetter
import com.example.demo.model.Query

class GenCreateDAO(
    private val config: DAOCreate
) {
    private var code = ""

    fun generate(): String {
        code = """
                package ${config.getPath()}

                import androidx.room.*
                import androidx.room.Dao
                ${generateImports()}

                @Dao
                interface ${CapitalizeFirstLetter().uppercaseChar(config.getName())} {
                    ${generateQueries(config.getListQuery())}
                }
            """.trimIndent()

        return code
    }

    private fun generateImports(): String {
        var content = ""
        val uniquePackages = mutableSetOf<String>()

        for (i in config.getListQuery().indices) {
            val packageFile = config.getListQuery()[i].packageFile

            if (uniquePackages.add(packageFile)) { // Checking for repetition
                content += """import ${config.getListQuery()[i].packageFile}
                """
            }
        }
        return content
    }

    private fun generateQueries(list: List<Query>): String {
        var content = ""

        for (i in list.indices) {
            if (i >= 1) {
                content += """
                    
                """
            }
            var cont = ""
            val typeQuery = list[i].typeQuery
            val query = list[i].query
            val name = list[i].name
            val nameFile = list[i].nameChooseFile

            if (query != true) {
                //val entity = list[i].entity
                //val onConflict = list[i].onConflict

                cont = when (typeQuery) {
                    "AllEntity" -> """
                    @Query("SELECT * FROM ${CapitalizeFirstLetter().lowercaseChar(nameFile)}s")
                    fun $name(): List<${
                        CapitalizeFirstLetter().uppercaseChar(
                            nameFile
                        )
                    }>"""

                    "ListEntitysEmpty" -> """
                    @Query("SELECT * from ${CapitalizeFirstLetter().lowercaseChar(nameFile)}s LIMIT 1")
                    fun $name(): ${
                        CapitalizeFirstLetter().uppercaseChar(
                            nameFile
                        )
                    }?"""

                    "SearchEntitysByNameLetter" -> """
                    @Query("SELECT * FROM ${CapitalizeFirstLetter().lowercaseChar(nameFile)}s WHERE name LIKE '%' || :${
                        CapitalizeFirstLetter().lowercaseChar(
                            nameFile
                        )
                    } || '%'")
                    fun $name(${CapitalizeFirstLetter().lowercaseChar(nameFile)}: String): List<${
                        CapitalizeFirstLetter().uppercaseChar(
                            nameFile
                        )
                    }>"""

                    "Delete" -> """
                    @$typeQuery(entity = ${CapitalizeFirstLetter().uppercaseChar(nameFile)}::class)
                    fun $name(${CapitalizeFirstLetter().lowercaseChar(nameFile)}: ${
                        CapitalizeFirstLetter().uppercaseChar(
                            nameFile
                        )
                    })"""
                    // Other options to choose from
                    else -> """
                    @$typeQuery(entity = ${CapitalizeFirstLetter().uppercaseChar(nameFile)}::class${
                        if (list[i].onConflict != null) {
                            ", onConflict = OnConflictStrategy.${config.getListQuery()[i].onConflict}"
                        } else {
                            ""
                        }
                    })
                    fun $name(${CapitalizeFirstLetter().lowercaseChar(nameFile)}: ${
                        CapitalizeFirstLetter().uppercaseChar(
                            nameFile
                        )
                    })"""
                }

            } else {
                val valueQuery = list[i].valueQuery
                cont = """
                    @$typeQuery("$valueQuery")
                    fun $name(${CapitalizeFirstLetter().lowercaseChar(nameFile)}: ${
                    CapitalizeFirstLetter().uppercaseChar(
                        config.getFileChooseName()
                    )
                })"""
            }

            content += cont
        }
        return content
    }

    private fun columnQuestionMark(b: Boolean): String {
        return if (b) "?" else ""
    }
}