package com.example.demo.model

data class PrimaryKey(
    val name: String,
    val dataType: String,
    val autoGenerateValue: Boolean,
    val value: String
)