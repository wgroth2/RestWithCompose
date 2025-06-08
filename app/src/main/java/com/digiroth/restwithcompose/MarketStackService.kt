package com.digiroth.restwithcompose

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MarketStackService {
    @GET("eod") // Endpoint path relative to base URL. For v1/eod, it's just "eod"
    suspend fun getEndOfDayData(
        @Query("access_key") accessKey: String,
        @Query("symbols") symbols: String, // The ticker symbol
        @Query("date_from") dateFrom: String? = null, // Optional: YYYY-MM-DD
        @Query("date_to") dateTo: String? = null,     // Optional: YYYY-MM-DD
        @Query("limit") limit: Int? = null,           // Optional
        @Query("offset") offset: Int? = null          // Optional
        // Add other optional parameters as needed: sort, exchange, etc.
    ): Response<EndOfDayResponse> // Using Response<> for more control over error handling
}