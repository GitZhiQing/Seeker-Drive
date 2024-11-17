package com.seeker.drive.data

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part

interface ApiService {
    @FormUrlEncoded
    @POST("auth/token")
    fun login(
        @Field("grant_type") grantType: String,
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<OAuth2LoginResponse>

    @POST("users/")
    fun register(@Body user: UserCreate): Call<User>

    @GET("users/current")
    fun getCurrentUser(@Header("Authorization") token: String): Call<User>

    @Multipart
    @PUT("users/current/avatar")
    fun updateAvatar(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): Call<User>
}