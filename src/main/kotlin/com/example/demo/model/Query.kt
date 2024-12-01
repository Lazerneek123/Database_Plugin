package com.example.demo.model

data class Query(
    val typeQuery: String,
    val query: Boolean,
    val name: String,
    val entity: String?,
    val onConflict: String?,
    val valueQuery: String?
)