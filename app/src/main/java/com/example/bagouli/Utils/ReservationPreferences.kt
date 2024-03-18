package com.example.bagouli.Utils

import android.content.Context
import android.preference.PreferenceManager
import com.example.bagouli.Data.Reservation
import com.google.gson.Gson
import java.util.prefs.Preferences

object ReservationPreferences {
    private const val KEY_RESERVATION = "reservation"
    fun saveReservation(context: Context, reservation: Reservation){
        val json = Gson().toJson(reservation)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferences.edit().putString(KEY_RESERVATION, json).apply()
    }
    fun getReservation(context: Context): Reservation? {
        val json = PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_RESERVATION, null)
        return Gson().fromJson(json, Reservation::class.java)
    }
}