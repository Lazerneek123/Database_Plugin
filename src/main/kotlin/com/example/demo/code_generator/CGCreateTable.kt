package com.example.demo.code_generator

import com.example.demo.model.ColumnData

class CGCreateTable {
    private var content = ""

    fun generate(
        path: String,
        tableName: String,
        primaryKeyName: String,
        primaryKeyAutoGenerate: Boolean,
        primaryKeyValue: String,
        listColumnData: List<ColumnData>
    ): String {
        content = """
                package $path

                import androidx.room.ColumnInfo
                import androidx.room.Entity
                import androidx.room.PrimaryKey

                @Entity(tableName = "$tableName")
                data class $tableName(
                   ${generateColumns(listColumnData)}
                ) {
                   @PrimaryKey(autoGenerate = ${primaryKeyAutoGenerate})
                   var $primaryKeyName: Int = ${
            if (!primaryKeyAutoGenerate) {
                primaryKeyValue
            } else {
                0
            }
        }
                }
            """.trimIndent()

        return content
    }

    private fun generateColumns(list: List<ColumnData>): String {
        var content = ""

        for (i in list.indices) {
            val cont = """
                @ColumnInfo(name = "${list[i].name}")
                val ${list[i].name}: ${list[i].dataType} = ${list[i].value}
            """
            content += cont

            if (i < list.size - 1) {
                content += ","
            }
        }
        return content
    }
}