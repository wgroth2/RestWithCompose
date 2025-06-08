package com.digiroth.restwithcompose // Or your chosen package name

import com.digiroth.restwithcompose.EndOfDayResponse
import com.digiroth.restwithcompose.Pagination
import com.digiroth.restwithcompose.EndOfDayData
import com.digiroth.restwithcompose.MarketStackService
import com.digiroth.restwithcompose.RetrofitInstance // Your Retrofit setup
import retrofit2.Response

class MarketStackRepository {

    suspend fun getEndOfDayData(
        accessKey: String,
        symbols: String,
        baseUrl: String,
        dateFrom: String? = null,
        dateTo: String? = null,
        limit: Int? = 1
    ): Result<EndOfDayResponse> { // Using Kotlin's Result type
        return try {
            // Create the service instance using the dynamic base URL
            val service = RetrofitInstance.createService(MarketStackService::class.java, baseUrl)

            val response: Response<EndOfDayResponse> = service.getEndOfDayData(
                accessKey = accessKey,
                symbols = symbols,
                dateFrom = dateFrom,
                dateTo = dateTo,
                limit = limit
            )

            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Response body is null for symbols: $symbols"))
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Unknown API error (code: ${response.code()})"
                Result.failure(Exception("API Error ${response.code()}: $errorMsg for symbols: $symbols"))
            }
        } catch (e: Exception) {
            // Log the exception for more details during debugging
            // Log.e("MarketStackRepository", "Network/Serialization Error for $symbols", e)
            Result.failure(Exception("Network or data processing error for $symbols: ${e.localizedMessage}", e))
        }
    }
}