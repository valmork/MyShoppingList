package com.example.myshoppinglist.domain

// POJO - класс
data class ShopItem(
    val id: Int,
    val name: String,
    val count: Int,
    val enabled: Boolean
)
