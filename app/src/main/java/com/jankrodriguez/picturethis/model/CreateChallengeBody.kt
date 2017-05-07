package com.jankrodriguez.picturethis.model

data class CreateChallengeBody(
    val challenger_id: String,
    val title: String,
    val lat: Float,
    val long: Float,
    val picture_url: String,
    val is_global: Boolean,
    val icon: String? = null,
    val challenged_ids: List<String>? = null)
