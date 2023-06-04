package com.example.meongku.api

import com.example.meongku.preference.UserPreferences
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient(private val userPreferences: UserPreferences) {

    private val authInterceptor = Interceptor { chain ->
        val req = chain.request()
        val idToken = userPreferences.idToken ?: ""
        val requestHeaders = req.newBuilder()
            .addHeader("Authorization", "$idToken")
            .build()
        chain.proceed(requestHeaders)
    }
    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://c76db54d862b-wgapj5fkzq-et.a.run.app/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    fun apiInstance(): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}