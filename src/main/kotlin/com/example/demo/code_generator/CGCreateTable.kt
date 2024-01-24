package com.example.demo.code_generator

class CGCreateTable {
    private var content = ""

    fun generate(
        path: String,
        tableName: String,
        primaryKeyName: String,
        primaryKeyAutoGenerate: Boolean,
        primaryKeyValue: String
    ): String {
        content = """
                package $path

                import androidx.room.ColumnInfo
                import androidx.room.Entity
                import androidx.room.PrimaryKey

                @Entity(tableName = "$tableName")
                data class $tableName(
                   @ColumnInfo(name = "title")
                   val title: String = "",
                   @ColumnInfo(name = "url")
                   val url: String = ""
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
}