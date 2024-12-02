package com.example.android.politicalpreparedness.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class CivicsHttpClient : OkHttpClient() {
    companion object {
        private val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        fun getClient(): OkHttpClient {
            return Builder()
                .addInterceptor(ApiRequestInterceptor())
                .addInterceptor(loggingInterceptor)
                .build()
        }
    }

}