package com.example.demo.generator

import com.example.demo.daoConfig.DAOCreate
import com.example.demo.element.CapitalizeFirstLetter
import com.example.demo.model.Query
import com.example.demo.model.QueryType
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtModifierListOwner

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
            val typeQuery = list[i].queryCategory
            val queryType = list[i].queryType
            val name = list[i].name
            val nameFile = list[i].nameChooseFile
            val ktFile = list[i].ktFile

            if (queryType == QueryType.TEMPLATE) {
                //val entity = list[i].entity
                //val onConflict = list[i].onConflict

                cont = when (typeQuery) {
                    "AllEntity" -> {
                        val rawTableName = findEntityTableName(ktFile, nameFile).trim()
                            .replace("\"", "") // Видаляємо зайві лапки, якщо є
                        val tableName = CapitalizeFirstLetter().lowercaseChar(rawTableName) //
                        //val tableName = CapitalizeFirstLetter().lowercaseChar(findEntityTableName(ktFile, name))
                        """
                    @Query("SELECT * FROM $tableName")
                    fun $name(): List<${CapitalizeFirstLetter().uppercaseChar(nameFile)}>"""
                    }

                    "ListEntitiesEmpty" -> {
                        val rawTableName = findEntityTableName(ktFile, nameFile).trim()
                            .replace("\"", "") // Видаляємо зайві лапки, якщо є
                        val tableName = CapitalizeFirstLetter().lowercaseChar(rawTableName)

                        """
                    @Query("SELECT * from $tableName LIMIT 1")
                    fun $name(): ${
                            CapitalizeFirstLetter().uppercaseChar(
                                nameFile
                            )
                        }?"""
                    }

                    "SearchByLetter" -> {
                        val raw = findEntityTableName(ktFile, nameFile).trim()
                            .replace("\"", "") // Видаляємо зайві лапки, якщо є
                        val tableName = CapitalizeFirstLetter().lowercaseChar(raw)
                        val columnName = list[i].columnSelect!!

                        """
                    @Query("SELECT * FROM $tableName WHERE $columnName LIKE '%' || :${
                            CapitalizeFirstLetter().lowercaseChar(
                                columnName
                            )
                        } || '%'")
                    fun $name(${CapitalizeFirstLetter().lowercaseChar(columnName)}: String): List<${
                            CapitalizeFirstLetter().uppercaseChar(
                                nameFile
                            )
                        }>"""
                    }

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

            }
            if (queryType == QueryType.MANUAL) {
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

    private fun findEntityTableName(ktFile: KtFile?, nameClass: String): String {
        // Обходимо всі декларації у файлі
        if (ktFile != null) {
            ktFile.declarations.forEach { declaration ->
                // Приведення до KtModifierListOwner для доступу до анотацій
                val annotations = (declaration as? KtModifierListOwner)?.annotationEntries ?: return@forEach

                // Шукаємо анотацію @Entity
                annotations.forEach { annotation ->
                    val annotationName = annotation.shortName?.asString()
                    if (annotationName == "Entity") {
                        // Знаходимо атрибут tableName
                        annotation.valueArguments.forEach { argument ->
                            val name = argument.getArgumentName()?.asName?.asString()
                            return if (name == "tableName") {
                                argument.getArgumentExpression()?.text.toString() // Повертаємо знайдене значення tableName
                            } else {
                                nameClass
                            }
                        }
                    }
                }
            }
            return nameClass
        } else {
            return nameClass
        }
    }

    private fun columnQuestionMark(b: Boolean): String {
        return if (b) "?" else ""
    }
}