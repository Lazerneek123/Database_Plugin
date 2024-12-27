package com.example.demo.model

data class ForeignKey(
    val entity: String?,
    val parentColumns: List<String?>?,
    val childColumns: List<String?>?,
    val onDelete: String?,
    val onUpdate: String?,
    val deferred: String?
)