package com.example.demo.tableConfig

import com.example.demo.model.Column
import com.example.demo.model.PrimaryKey

data class Relation(
    private var path: String,
    private var tableName1: String,
    private var tableName2: String,
    private var tableRelationName: String,
    private var primaryKeyData1: PrimaryKey,
    private var primaryKeyData2: PrimaryKey,
    private var listColumnData: List<Column>
) {

    // Getters
    fun getPath(): String {
        return path
    }

    fun getTableName1(): String {
        return tableName1
    }

    fun getTableName2(): String {
        return tableName2
    }

    fun getTableRelationName(): String {
        return tableRelationName
    }

    fun getPrimaryKeyData1(): PrimaryKey {
        return primaryKeyData1
    }

    fun getPrimaryKeyData2(): PrimaryKey {
        return primaryKeyData2
    }

    fun getListColumnData(): List<Column> {
        return listColumnData
    }

    // Setters
    fun setPath(newPath: String) {
        path = newPath
    }

    fun setTableName1(newTableName1: String) {
        tableName1 = newTableName1
    }

    fun setTableName2(newTableName2: String) {
        tableName2 = newTableName2
    }

    fun setTableRelationName(newTableRelationName: String) {
        tableRelationName = newTableRelationName
    }

    fun setPrimaryKeyData1(newPrimaryKeyData1: PrimaryKey) {
        primaryKeyData1 = newPrimaryKeyData1
    }

    fun setPrimaryKeyData2(newPrimaryKeyData2: PrimaryKey) {
        primaryKeyData2 = newPrimaryKeyData2
    }

    fun setListColumnData(newListColumnData: List<Column>) {
        listColumnData = newListColumnData
    }
}
