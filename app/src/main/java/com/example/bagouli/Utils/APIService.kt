package com.example.bagouli.Utils

import com.example.bagouli.Data.Cars
import com.example.bagouli.Data.ClientRequest
import com.example.bagouli.Data.ForgotPasswordRequest
import com.example.bagouli.Data.LoginRequest
import com.example.bagouli.Data.RegisterRequest
import com.example.bagouli.Data.ReservationRequest
import com.example.bagouli.Data.ResetPasswordRequest
import com.example.bagouli.Data.TokenRequest
import com.example.bagouli.DataModels.ForgotPasswordResponse
import com.example.bagouli.DataModels.LoginResponse
import com.example.bagouli.DataModels.RegisterResponse
import com.example.bagouli.DataModels.ReservationResponse
import com.example.bagouli.DataModels.UpdateProfileResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface APIService {

    @POST("register")
    fun register(@Body registerRequest: RegisterRequest): Call<RegisterResponse>

    @POST("login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @PUT("users/{user_id}")
    fun updateProfile(@Path("user_id") userId: Int, @Body clientRequest: ClientRequest) : Call<UpdateProfileResponse>

    @POST("forgot-password")
    fun forgotPassword(@Body forgotPasswordRequest: ForgotPasswordRequest): Call<ForgotPasswordResponse>

    @POST("reset-password")
    fun resetPassword(@Body resetPasswordRequest: ResetPasswordRequest): Call<ForgotPasswordResponse>

    @POST("logout")
    fun logout(@Body tokenRequest: TokenRequest): Call<Unit>

    @GET("cars")
    fun getCars(): Call<List<Cars>>

    @POST("reservations/{car_id}")
    fun reserveCar(@Path("car_id") carId: Int, @Body reservationRequest: ReservationRequest): Call<ReservationResponse>

    @GET("reservations/{user_id}")
    fun showReservation(@Path("user_id") userId: Int): Call<ReservationResponse>

    @DELETE("reservations/{reservation_id}")
    fun deleteReservation(@Path("reservation_id") reservationId: Int): Call<Void>

}