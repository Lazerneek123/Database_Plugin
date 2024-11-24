package com.example.demo.generator

import com.example.demo.element.CapitalizeFirstLetter
import com.example.demo.model.PrimaryKey

class GenCreateTableRelation {
    private var code = ""

    fun generate(
        path: String,
        tableName1: String,
        tableName2: String,
        tableRelationName: String,
        primaryKeyTable1: PrimaryKey,
        primaryKeyTable2: PrimaryKey
    ): String {
        code = """
                package $path

                import androidx.room.Entity
                import androidx.room.ForeignKey

                @Entity(
                    tableName = "$tableRelationName",
                    primaryKeys = ["${primaryKeyTable1.name}", "${primaryKeyTable2.name}"]
                )
                data class ${CapitalizeFirstLetter().uppercaseChar(tableRelationName)}(
                    val ${primaryKeyTable1.name}: ${primaryKeyTable1.dataType},
                    val ${primaryKeyTable2.name}: ${primaryKeyTable2.dataType}
                )
            """.trimIndent()

        return code
    }
}