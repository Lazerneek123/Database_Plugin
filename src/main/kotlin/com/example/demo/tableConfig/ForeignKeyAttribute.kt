package com.example.demo.tableConfig

class ForeignKeyAttribute {
    private val attribute = arrayOf(
        "entity",
        "parentColumns",
        "childColumns",
        "onDelete",
        "onUpdate",
        "deferred"
    )

    private val action = arrayOf(
        "NO_ACTION",
        "RESTRICT",
        "SET_NULL",
        "SET_DEFAULT",
        "CASCADE"
    )

    fun get(): Array<String> {
        return attribute
    }

    fun getAction(): Array<String> {
        return action
    }
}