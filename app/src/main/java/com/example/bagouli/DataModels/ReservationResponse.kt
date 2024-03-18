package com.example.bagouli.DataModels

import com.example.bagouli.Data.Reservation

data class ReservationResponse(val message: String, val reservations: List<Reservation>)
