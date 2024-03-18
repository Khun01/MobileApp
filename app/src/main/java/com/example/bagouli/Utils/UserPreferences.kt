package com.example.bagouli.Utils

import android.content.Context
import com.example.bagouli.Data.UserRequest
import com.example.bagouli.DataModels.LoginResponse
import com.google.gson.Gson

object UserPreferences {
    private const val PREF_NAME = "UserPrefs"
    private const val KEY_USER = "user"
    private const val KEY_USER_ROLE = "role"
    private const val KEY_ACCESS_TOKEN = "accessToken"

    fun saveUserData(context: Context, loginResponse: LoginResponse){
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val userJson = Gson().toJson(loginResponse.user)
        with(sharedPreferences.edit()){
            putString(KEY_USER, userJson)
            putString(KEY_USER_ROLE, loginResponse.role)
            putString(KEY_ACCESS_TOKEN, loginResponse.token)
            apply()
        }
    }
    fun getUserInfo(context: Context) : UserRequest? {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val userInfoString = sharedPreferences.getString(KEY_USER, null)
        return Gson().fromJson(userInfoString, UserRequest::class.java)
    }
    fun getToken(context: Context) : String? {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
    }
    fun clearUserData(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
    }
}