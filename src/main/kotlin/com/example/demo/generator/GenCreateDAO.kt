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
                import ${config.getPathChooseFile()}

                @Dao
                interface ${CapitalizeFirstLetter().uppercaseChar(config.getName())} {
                    ${generateQueries(config.getListQuery())}
                }
            """.trimIndent()

        return code
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

            if (query != true) {
                //val entity = list[i].entity
                //val onConflict = list[i].onConflict

                cont = when (typeQuery) {
                    "AllUsers" -> """
                    @Query("SELECT * FROM ${CapitalizeFirstLetter().lowercaseChar(config.getFileChooseName())}s")
                    fun $name(): List<${
                        CapitalizeFirstLetter().uppercaseChar(
                            config.getFileChooseName()
                        )
                    }>"""

                    "ListUsersEmpty" -> """
                    @Query("SELECT * from ${CapitalizeFirstLetter().lowercaseChar(config.getFileChooseName())}s LIMIT 1")
                    fun $name(): ${
                        CapitalizeFirstLetter().uppercaseChar(
                            config.getFileChooseName()
                        )
                    }?"""

                    "SearchUsersByNameLetter" -> """
                    @Query("SELECT * FROM ${CapitalizeFirstLetter().lowercaseChar(config.getFileChooseName())}s WHERE name LIKE '%' || :${
                        CapitalizeFirstLetter().lowercaseChar(
                            config.getFileChooseName()
                        )
                    } || '%'")
                    fun $name(${CapitalizeFirstLetter().lowercaseChar(config.getFileChooseName())}: String): List<${
                        CapitalizeFirstLetter().uppercaseChar(
                            config.getFileChooseName()
                        )
                    }>"""
                    // Other options to choose from
                    else -> """
                    @$typeQuery(entity = ${CapitalizeFirstLetter().uppercaseChar(config.getFileChooseName())}::class, onConflict = OnConflictStrategy.REPLACE)
                    fun $name(${CapitalizeFirstLetter().lowercaseChar(config.getFileChooseName())}: ${
                        CapitalizeFirstLetter().uppercaseChar(
                            config.getFileChooseName()
                        )
                    })"""
                }

            } else {
                val valueQuery = list[i].valueQuery
                cont = """
                    @$typeQuery("$valueQuery")
                    fun $name(${CapitalizeFirstLetter().lowercaseChar(config.getFileChooseName())}: ${
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