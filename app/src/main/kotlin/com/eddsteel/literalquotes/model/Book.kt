package com.eddsteel.literalquotes.model

import kotlinx.serialization.Serializable

@Serializable
data class Book(
    val isbn: String,
    val quotes: List<String>,
    val author: String,
    val title: String,
    val year: Int? = null,
    val asin: String? = null
)
