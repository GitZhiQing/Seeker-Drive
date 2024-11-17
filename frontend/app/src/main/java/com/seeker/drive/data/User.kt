package com.seeker.drive.data

data class User(
    val uid: String,
    val username: String,
    val avatar: String?,
    val register_time: Long?
)