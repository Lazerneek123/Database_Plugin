package com.example.demo.generator

import com.example.demo.input.InputDialogRelation

fun entityRelation(inputDialog: InputDialogRelation): String {
    return """
                @Entity(
                    tableName = "${inputDialog.getTableName2()}",
                    foreignKeys = [ForeignKey(
                        entity = ${inputDialog.getClassName1()}::class,
                        parentColumns = ["${inputDialog.getParentColumn()}"],
                        childColumns = ["${inputDialog.getEntityColumn()}"],
                        onDelete = ForeignKey.CASCADE
                    )]
                )
            """.trimIndent()
}