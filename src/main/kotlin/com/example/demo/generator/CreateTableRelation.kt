package com.example.demo.generator

import com.example.demo.element.CapitalizeFirstLetter
import com.example.demo.model.PrimaryKey

class CreateTableRelation {
    private var relationContent = ""

    fun generate(
        path: String,
        tableName1: String,
        tableName2: String,
        tableRelationName: String,
        primaryKeyTable1: PrimaryKey,
        primaryKeyTable2: PrimaryKey
    ): String {
        relationContent = """
                package $path

                import androidx.room.Entity
                import androidx.room.ForeignKey

                @Entity(
                    tableName = "$tableRelationName",
                    primaryKeys = ["${primaryKeyTable1.name}", "${primaryKeyTable2.name}"],
                    foreignKeys = [
                        ForeignKey(
                            entity = ${tableName1}::class,
                            parentColumns = ["${primaryKeyTable1.name}_"],
                            childColumns = ["${primaryKeyTable1.name}"]
                        ),
                        ForeignKey(
                            entity = ${tableName2}::class,
                            parentColumns = ["${primaryKeyTable2.name}_"],
                            childColumns = ["${primaryKeyTable2.name}"]
                        )
                    ]
                )
                data class ${CapitalizeFirstLetter().setString(tableRelationName)}(
                    val ${primaryKeyTable1.name}: ${primaryKeyTable1.dataType},
                    val ${primaryKeyTable2.name}: ${primaryKeyTable2.dataType}
                )
            """.trimIndent()

        return relationContent
    }
}