package com.example.demo.generator

import com.example.demo.element.CapitalizeFirstLetter
import com.example.demo.model.Column
import com.example.demo.model.PrimaryKey

class CreateTableForRelation {
    private var code = ""

    fun generate(
        path: String,
        tableName1: String,
        tableName2: String,
        tableRelationName: String,
        primaryKeyData1: PrimaryKey,
        primaryKeyData2: PrimaryKey,
        listColumnData: List<Column>
    ): String {
        code = """
                package $path

                import androidx.room.ColumnInfo
                import androidx.room.Entity
                import androidx.room.Junction
                import androidx.room.PrimaryKey
                import androidx.room.Relation

                @Entity(tableName = "$tableName1")
                data class ${CapitalizeFirstLetter().uppercaseChar(tableName1)}(
                ${generatePrimaryKey(primaryKeyData1)}
                ${generateColumns(listColumnData)}
                ) {
                    @Relation(
                        parentColumn = "${primaryKeyData1.name}_",
                        entityColumn = "${primaryKeyData2.name}_",
                        associateBy = Junction(${tableRelationName}::class)
                    )
                    var ${tableName2}s: List<${CapitalizeFirstLetter().uppercaseChar(tableName2)}> = listOf()
                }
            """.trimIndent()

        return code
    }

    private fun generatePrimaryKey(primaryKey: PrimaryKey): String {
        var content = ""

        val autoGenerate = primaryKey.autoGenerateValue
        val name = primaryKey.name
        val dataType = primaryKey.dataType
        val value = primaryKey.value

        val cont = """    @PrimaryKey(autoGenerate = $autoGenerate)
                    @ColumnInfo(name = "$name")
                    var $name: $dataType = $value,"""
        content += cont

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
}