package com.example.demo.tableConfig

class ColumnAttribute {
    private val attribute = arrayOf(
        "name",
        "typeAffinity",
        "index",
        "defaultValue",
        "collate"
    )

    private val dataType = arrayOf(
        "INTEGER",
        "TEXT",
        "REAL",
        "BLOB"
    )

    private val comparativeSchema = arrayOf(
        "NOCASE",
        "BINARY",
        "LOCALIZED",
        "RTRIM"
    )

    fun get(): Array<String> {
        return attribute
    }

    fun getDataType(): Array<String> {
        return dataType
    }

    fun getComparativeSchema(): Array<String> {
        return comparativeSchema
    }
}