package com.example.demo.tableConfig

import com.example.demo.model.Column
import com.example.demo.model.PrimaryKey

data class TableCreate(
    private var path: String,
    private var tableName: String,
    private var listPrimaryKeyData: List<PrimaryKey>,
    private var listColumnData: List<Column>
) {
    fun getPath(): String = path
    fun setPath(newPath: String) {
        path = newPath
    }

    fun getTableName(): String = tableName
    fun setTableName(newTableName: String) {
        tableName = newTableName
    }

    fun getListPrimaryKeyData(): List<PrimaryKey> = listPrimaryKeyData
    fun setListPrimaryKeyData(newListPrimaryKeyData: List<PrimaryKey>) {
        listPrimaryKeyData = newListPrimaryKeyData
    }

    fun getListColumnData(): List<Column> = listColumnData
    fun setListColumnData(newListColumnData: List<Column>) {
        listColumnData = newListColumnData
    }
}
