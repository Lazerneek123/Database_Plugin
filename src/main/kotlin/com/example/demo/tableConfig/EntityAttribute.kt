package com.example.demo.tableConfig

class EntityAttribute {
    private val attribute = arrayOf(
        "tableName",
        "primaryKeys",
        "foreignKeys",
        "indices",
        "inheritSuperIndices",
        "ignoredColumns"
    )

    fun get(): Array<String> {
        return attribute
    }
}