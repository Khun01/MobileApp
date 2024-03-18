package com.example.bagouli.Data

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Reservation(@SerializedName("id") val id: Int,
                       @SerializedName("user_id") val user_id: Int,
                       @SerializedName("car_id") val car_id: Int,
                       @SerializedName("start_date") val start_date: String,
                       @SerializedName("end_date") val end_date: String,
                       @SerializedName("days") val days: String,
                       @SerializedName("price_per_day") val price_per_day: Double,
                       @SerializedName("total_price") val total_price: Double,
                       @SerializedName("status") val status: String,
                       @SerializedName("payment_method") val payment_method: String,
                       val car: Cars) : Serializable