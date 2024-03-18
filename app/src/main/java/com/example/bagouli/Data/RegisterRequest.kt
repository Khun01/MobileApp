package com.example.bagouli.Data

data class RegisterRequest(val name: String, val email: String,
    val password: String, val password_confirmation: String)
