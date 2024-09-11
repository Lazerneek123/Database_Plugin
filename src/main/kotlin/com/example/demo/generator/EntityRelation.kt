package com.example.demo.generator

import com.example.demo.input.InputDialogRelationOneToOne

fun entityRelation(inputDialog: InputDialogRelationOneToOne): String {
    return """
                @Entity(
                    tableName = "${inputDialog.getTableName1()}",
                    foreignKeys = [ForeignKey(
                        entity = User::class,
                        parentColumns = ["${inputDialog.getParentColumn()}"],
                        childColumns = ["${inputDialog.getEntityColumn()}"],
                        onDelete = ForeignKey.CASCADE
                    )]
                )
            """.trimIndent()
}