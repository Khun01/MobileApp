package com.example.bagouli.Data

data class ResetPasswordRequest(val email: String, val token: String, val password: String,
                                val password_confirmation: String)
