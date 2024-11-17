package com.seeker.drive.data

data class OAuth2LoginResponse(val access_token: String, val token_type: String) {
    fun isValid(): Boolean {
        return access_token.isNotEmpty() && token_type.isNotEmpty()
    }
}
