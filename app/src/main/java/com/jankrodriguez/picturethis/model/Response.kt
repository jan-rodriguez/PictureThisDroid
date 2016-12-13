package com.jankrodriguez.picturethis.model

data class Response(
    val id: Int,
    val challengeId: Int,
    val userId: Int,
    val status: Status) {
    enum class Status {
        open,
        accepted,
        declined
    }
}
