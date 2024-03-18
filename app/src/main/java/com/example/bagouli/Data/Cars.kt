package com.example.bagouli.Data

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Cars(@SerializedName("id") val id: Int,
                @SerializedName("brand") val brand: String,
                @SerializedName("model") val model: String, val engine: String?, val price_per_day: Double,
                val image: String, val quantity: String, val status: String, val stars: Double, val description: String) : Serializable
