package com.example.bagouli

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.bagouli.Data.Reservation
import com.example.bagouli.FirstPage.MainActivity
import com.example.bagouli.Utils.APIClient
import com.example.bagouli.Utils.ReservationPreferences
import com.example.bagouli.databinding.ActivityReservationInfoBinding
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReservationInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReservationInfoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReservationInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.cancelBTN.setOnClickListener {
            showDeleteConfirmationDialog()
        }
        binding.cd1.setOnClickListener {
            onBackPressed()
        }
        reservation()
    }
    private fun reservation(){
        val reserveCars = intent.getSerializableExtra("reserveCar") as Reservation

        Picasso.get().load(reserveCars.car.image)
            .memoryPolicy(MemoryPolicy.NO_CACHE)
            .networkPolicy(NetworkPolicy.NO_CACHE)
            .into(binding.image)
        binding.model.text = reserveCars.car.model
        binding.brand.text = reserveCars.car.brand
        binding.totalPrice.text = reserveCars.total_price.toString()
        binding.dateEnd.text = reserveCars.end_date
        binding.dateStart.text = reserveCars.start_date
        binding.status.text = reserveCars.status
    }

    private fun showDeleteConfirmationDialog(){
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Delete Item")
            .setMessage("Are you sure you want to cancel your reservation?")
            .setPositiveButton("Yes"){ dialog, _ ->
                val reservationId = ReservationPreferences.getReservation(this)?.id
                val call = APIClient.getService().deleteReservation(reservationId!!)
                call.enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful){
                            val intent = Intent(this@ReservationInfoActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                            Toast.makeText(this@ReservationInfoActivity, "Reservation deleted successfully", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(this@ReservationInfoActivity, "You can't cancel, the reservation is already active", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(this@ReservationInfoActivity, "Network Error, Please try again later", Toast.LENGTH_SHORT).show()
                    }
                })
                dialog.dismiss()
            }
            .setNegativeButton("No"){ dialog, _ ->
                dialog.dismiss()
            }
            .create()
        alertDialog.show()
    }
}