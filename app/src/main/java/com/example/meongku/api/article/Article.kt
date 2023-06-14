package com.example.meongku.api.article

data class Article(
    val id: String,
    val createdAt: CreatedAt,
    val articleBody: String,
    val articleImage: String,
    val articleTitle: String,
    val articleCategory: List<String>
)
