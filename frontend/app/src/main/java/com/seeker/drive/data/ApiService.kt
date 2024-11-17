package com.seeker.drive.data

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

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

    @GET("files/")
    fun getFilesList(
        @Header("Authorization") token: String,
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 100
    ): Call<List<FileItem>>

    @Multipart
    @POST("files/")
    fun uploadFile(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): Call<FileItem>

    @GET("files/{fid}")
    fun downloadFile(
        @Header("Authorization") token: String,
        @Path("fid") fileId: Int
    ): Call<ResponseBody>

    @DELETE("files/{fid}")
    fun deleteFile(
        @Header("Authorization") token: String,
        @Path("fid") fileId: Int
    ): Call<Void>

    @PUT("files/{fid}/status")
    fun updateFileStatus(
        @Header("Authorization") token: String,
        @Path("fid") fid: Int,
        @Query("status") status: Int
    ): Call<Void>
}