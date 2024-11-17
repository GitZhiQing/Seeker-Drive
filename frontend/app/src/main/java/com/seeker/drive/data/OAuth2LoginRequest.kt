package com.seeker.drive.data


data class OAuth2LoginRequest(
    val grant_type: String = "password",
    val username: String,
    val password: String
) {
    fun isValid(): Boolean {
        return username.isNotEmpty() && password.isNotEmpty()
    }
}