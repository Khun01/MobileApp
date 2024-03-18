package com.example.bagouli.Utils

import android.annotation.SuppressLint
import android.preference.PreferenceManager
import android.util.Log
import com.example.bagouli.Data.Cars
import com.example.bagouli.DataModels.ReservationResponse
import com.google.gson.Gson

object CarPreferences {
    private const val PREF_NAME = "CarPreferences"
    private const val KEY_CAR = "KeyCar"

    @SuppressLint("CommitPrefEdits")
    fun saveCar(cars: Cars){
        val editor = PreferenceManager.getDefaultSharedPreferences(MyApp.getAppContext()).edit()
        editor.putString(KEY_CAR, Gson().toJson(cars))
        editor.apply()
    }
    fun saveReserveCars(cars: ReservationResponse?){
        val editor = PreferenceManager.getDefaultSharedPreferences(MyApp.getAppContext()).edit()
        editor.putString(KEY_CAR, Gson().toJson(cars))
        editor.apply()
        if (getCar() != null){
            Log.e("Cars", "Car saved successfully: $cars")
        }else{
            Log.e("Cars", "Failed to save car: $cars")
        }
    }
    fun getCarId(): Int?{
        val json = PreferenceManager.getDefaultSharedPreferences(MyApp.getAppContext()).getString(KEY_CAR, null)
        val car = Gson().fromJson(json, Cars::class.java)
        return car?.id
    }
    fun getCarPrice(): Double?{
        val car = getCar()
        return car?.price_per_day
    }
    private fun getCar(): Cars? {
        val json = PreferenceManager.getDefaultSharedPreferences(MyApp.getAppContext()).getString(KEY_CAR, null)
        return Gson().fromJson(json, Cars::class.java)
    }
}