package com.seeker.drive.data

import android.content.Context
import com.seeker.drive.R
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit

object RetrofitJsonInstance {
    private lateinit var baseUrl: String

    fun initialize(context: Context) {
        baseUrl = context.getString(R.string.api_protocol) +
                context.getString(R.string.api_host) +
                context.getString(R.string.api_port) +
                context.getString(R.string.api_prefix)
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}