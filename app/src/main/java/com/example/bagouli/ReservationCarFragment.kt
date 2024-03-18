package com.example.bagouli

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bagouli.Adapter.ReservationAdapter
import com.example.bagouli.Data.Reservation
import com.example.bagouli.DataModels.ReservationResponse
import com.example.bagouli.Utils.APIClient
import com.example.bagouli.Utils.ReservationPreferences
import com.example.bagouli.Utils.UserPreferences
import com.example.bagouli.databinding.FragmentReservationCarBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

class ReservationCarFragment : Fragment() {
    private lateinit var binding: FragmentReservationCarBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentReservationCarBinding.inflate(inflater,container,false)
        showReservations()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                showExitDialog()
            }
        })
        return binding.root
    }

    private fun popUpMenu(view: View, position: Int, reservation: Reservation){
        val popupMenu = PopupMenu(context, view)
        popupMenu.inflate(R.menu.reservation_list_menu)
        popupMenu.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.cancel -> {
                    showDeleteConfirmationDialog(position)
                    true
                }
                else -> true
            }
        }
        popupMenu.show()
    }
    private fun showDeleteConfirmationDialog(position: Int){
        val alertDialog = AlertDialog.Builder(context)
            .setTitle("Delete Item")
            .setMessage("Are you sure you want to delete this item?")
            .setPositiveButton("Delete"){ dialog, _ ->
                val reservationId = ReservationPreferences.getReservation(requireContext())?.id
                val call = APIClient.getService().deleteReservation(reservationId!!)
                call.enqueue(object : Callback<Void>{
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful){
                            val adapter = binding.recyclerView.adapter as ReservationAdapter
                            adapter.removeReservation(position)
                            adapter.notifyDataSetChanged()
                            Toast.makeText(context, "Reservation deleted successfully", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(context, "Failed to delete the reservation", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(context, "Network Error, Please try again later", Toast.LENGTH_SHORT).show()
                    }
                })
                dialog.dismiss()
            }
            .setNegativeButton("Cancel"){ dialog, _ ->
                dialog.dismiss()
            }
            .create()
        alertDialog.show()
    }

    private fun showReservations(){
        val userId = UserPreferences.getUserInfo(requireContext())!!.id
        val call = APIClient.getService().showReservation(userId)
        call.enqueue(object : Callback<ReservationResponse>{
            override fun onResponse(call: Call<ReservationResponse>, response: Response<ReservationResponse>) {
                if (response.isSuccessful){
                    val reservations = response.body()
                    reservations?.let {
                        val adapter = ReservationAdapter(reservations.reservations)
                        binding.recyclerView.layoutManager = LinearLayoutManager(context)
                        binding.recyclerView.adapter = adapter
                        adapter.onClickItem = { reserveCar ->
                            ReservationPreferences.saveReservation(requireContext(), reserveCar)
                            val intent = Intent(context, ReservationInfoActivity::class.java)
                            intent.putExtra("reserveCar", reserveCar)
                            startActivity(intent)
                        }
                        adapter.onMenuDotClick = { position, view, reservation ->
                            ReservationPreferences.saveReservation(requireContext(), reservation)
                            Log.d("Reservation Data", "Reservation Saved: $reservation")
                            popUpMenu(view, position, reservation)
                        }
                    }
                }else{
                    Toast.makeText(context, "No reservation found for the user", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ReservationResponse>, t: Throwable) {
                Toast.makeText(context, "Network Failed, Please try again later", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showExitDialog(){
        AlertDialog.Builder(context)
            .setTitle("Exit Confirmation")
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("Exit"){ dialog, which ->
                requireActivity().finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}