package com.example.bagouli.Data

import com.google.gson.annotations.SerializedName

data class ForgotPasswordRequest(@SerializedName("email") val email: String)
