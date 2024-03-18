package com.example.bagouli.DataModels

import com.example.bagouli.Data.UserRequest
import com.google.gson.annotations.SerializedName

data class LoginResponse(@SerializedName("user") val user: UserRequest,
                         @SerializedName("email") val email: String,
                         @SerializedName("role") val role: String,
                         @SerializedName("token") val token: String)
