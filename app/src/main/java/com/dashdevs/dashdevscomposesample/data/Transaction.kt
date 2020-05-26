package com.dashdevs.dashdevscomposesample.data

data class Transaction(
    val id: String,
    val iconUrl: String?,
    val amount: String,
    val title: String,
    val subtitle: String,
    val isDebit: Boolean
)