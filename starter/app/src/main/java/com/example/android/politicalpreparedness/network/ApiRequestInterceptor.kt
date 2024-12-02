package com.example.android.politicalpreparedness.network

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.HttpUrl


class ApiRequestInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url

        // Add query parameters
        val newUrl: HttpUrl = addQueryParameter(originalUrl, "key", API_KEY)
        val newRequest = originalRequest.newBuilder()
            .url(newUrl)
            .build()

        // Proceed with the modified request
        return chain.proceed(newRequest)
    }

    private fun addQueryParameter(url: HttpUrl, key: String, value: String): HttpUrl {
        return url.newBuilder()
            .addQueryParameter(key, value)
            .build()
    }

    companion object {
        private const val API_KEY = "AIzaSyCxcndiHeylMCtx2WDOqkqQ8Q2qTWy9yhI"
    }
}
