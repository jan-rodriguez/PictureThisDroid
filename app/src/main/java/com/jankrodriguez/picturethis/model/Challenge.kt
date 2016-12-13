package com.jankrodriguez.picturethis.model

data class Challenge(
    val id: Int,
    val icon: String,
    val isActive: Boolean,
    val location: Location,
    val pictureUrl: String,
    val title: String)
