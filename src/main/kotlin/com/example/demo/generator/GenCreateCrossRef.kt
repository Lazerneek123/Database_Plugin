package com.example.demo.generator

class GenCreateCrossRef {
    private lateinit var code: String
    private lateinit var nameClass: String

    fun getNameClass(): String {
        return nameClass
    }

    fun generate(
        path: String,
        crossRefName: String,
        parentColumn: String,
        entityColumn: String,
        parentColumnName: String,
        entityColumnName: String,
        dataTypeParent: String,
        dataTypeEntity: String
        ): String {
        nameClass = crossRefName
        code = """
                package $path

                import androidx.room.Entity
                
                @Entity(primaryKeys = ["$parentColumn", "$entityColumn"])
                data class $crossRefName(
                    val $parentColumnName: $dataTypeParent,
                    val $entityColumnName: $dataTypeEntity
                )""".trimIndent()

        return code
    }
}