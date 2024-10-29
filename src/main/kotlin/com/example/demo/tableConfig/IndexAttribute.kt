package com.example.demo.tableConfig

class IndexAttribute {
    private val attribute = arrayOf(
        "name",
        "value",
        "orders",
        "unique"
    )

    private val action = arrayOf(
        "ASC",
        "DESC"
    )

    fun get(): Array<String> {
        return attribute
    }

    fun getAction(): Array<String> {
        return action
    }
}