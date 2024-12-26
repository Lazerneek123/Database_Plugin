package com.example.demo.model

import org.jetbrains.kotlin.psi.KtFile

data class Query(
    val queryCategory: String,
    val queryType: QueryType,
    val name: String,
    val entity: String?,
    val onConflict: String?,
    val valueQuery: String?,
    val packageFile: String,
    val nameChooseFile: String,
    val ktFile: KtFile?,
    val columnSelect: String?
)

enum class QueryType {
    TEMPLATE,
    MANUAL
}