// In a new file, e.g., network/MarketStackModels.kt
package com.digiroth.restwithcompose // Or your preferred package

import com.google.gson.annotations.SerializedName // If using Gson
// import com.squareup.moshi.Json // If using Moshi

data class EndOfDayResponse(
    val pagination: Pagination,
    val data: List<EndOfDayData>
)

data class Pagination(
    val limit: Int,
    val offset: Int,
    val count: Int,
    val total: Int
)

data class EndOfDayData(
    val open: Double?, // Making nullable in case some values are missing
    val high: Double?,
    val low: Double?,
    val close: Double?,
    val volume: Double?, // API shows float, consider if Long is better if always whole number

    @SerializedName("adj_high") // Use if Gson and names differ
    // @Json(name = "adj_high") // Use if Moshi and names differ
    val adjHigh: Double?,

    @SerializedName("adj_low")
    // @Json(name = "adj_low")
    val adjLow: Double?,

    @SerializedName("adj_close")
    // @Json(name = "adj_close")
    val adjClose: Double?,

    @SerializedName("adj_open")
    // @Json(name = "adj_open")
    val adjOpen: Double?,

    @SerializedName("adj_volume")
    // @Json(name = "adj_volume")
    val adjVolume: Double?,

    val symbol: String,
    val exchange: String?,
    val date: String // Keep as String, parse to Date object if needed later
)