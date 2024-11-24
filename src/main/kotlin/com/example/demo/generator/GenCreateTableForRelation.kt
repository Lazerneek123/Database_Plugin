package com.example.demo.generator

import com.example.demo.element.CapitalizeFirstLetter
import com.example.demo.model.Column
import com.example.demo.model.PrimaryKey
import com.example.demo.tableConfig.Relation

class GenCreateTableForRelation {
    private var code = ""

    fun generate(
        config: Relation
    ): String {
        code = """
                package ${config.getPath()}

                import androidx.room.ColumnInfo
                import androidx.room.Entity
                import androidx.room.Junction
                import androidx.room.PrimaryKey
                import androidx.room.Relation

                @Entity(tableName = "${config.getTableName1()}")
                data class ${CapitalizeFirstLetter().uppercaseChar(config.getTableName1())}(
                ${generatePrimaryKey(config.getPrimaryKeyData1())}
                ${generateColumns(config.getListColumnData())}
                ) {
                    @Relation(
                        parentColumn = "${config.getPrimaryKeyData1().name}_",
                        entityColumn = "${config.getPrimaryKeyData2().name}_",
                        associateBy = Junction(${config.getTableRelationName()}::class)
                    )
                    var ${config.getTableName2()}s: List<${CapitalizeFirstLetter().uppercaseChar(config.getTableName2())}> = listOf()
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