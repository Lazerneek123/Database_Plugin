package com.example.demo.generator

import com.example.demo.element.CapitalizeFirstLetter
import com.example.demo.input.InputDialogRelationOneToOne

fun entityRelation(inputDialog: InputDialogRelationOneToOne): String {
    return """
                @Entity(
                    tableName = "${inputDialog.getTableName2()}",
                    foreignKeys = [ForeignKey(
                        entity = ${CapitalizeFirstLetter().uppercaseChar(inputDialog.getTableName1())}::class,
                        parentColumns = ["${inputDialog.getParentColumn()}"],
                        childColumns = ["${inputDialog.getEntityColumn()}"],
                        onDelete = ForeignKey.CASCADE
                    )]
                )
            """.trimIndent()
}