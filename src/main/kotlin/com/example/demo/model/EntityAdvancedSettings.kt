package com.example.demo.model

data class EntityAdvancedSettings(
    var tableName: String?,
    var primaryKeys: List<String?>?,
    var foreignKeys: List<ForeignKey?>?,
    var indices: List<Index?>?,
    var inheritSuperIndices: String?,
    var ignoredColumns: List<String?>?
)