package com.digiroth.restwithcompose

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// In a new file, e.g., network/RetrofitInstance.kt
// import retrofit2.converter.moshi.MoshiConverterFactory // If using Moshi
// import com.squareup.moshi.Moshi // If using Moshi
// import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory // If using Moshi
object RetrofitInstance {

    // You'll pass the baseUrl dynamically when creating the service instance
    //private const val BASE_URL = "http://api.marketstack.com/v2/" // Default, will be overridden

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // For seeing request/response logs
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor) // Add logging interceptor for debugging
        .build()

    // Function to create a Retrofit service instance with a dynamic base URL
    fun <T> createService(serviceClass: Class<T>, baseUrl: String): T {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl) // Use the dynamically provided base URL
            .client(httpClient) // Optional: For logging or other customizations
            .addConverterFactory(GsonConverterFactory.create()) // For Gson
            // .addConverterFactory(MoshiConverterFactory.create(
            //     Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            // )) // For Moshi
            .build()
        return retrofit.create(serviceClass)
    }
}