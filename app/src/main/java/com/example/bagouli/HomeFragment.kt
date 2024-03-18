package com.example.bagouli

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bagouli.Adapter.VehicleAdapter
import com.example.bagouli.Data.Cars
import com.example.bagouli.Utils.APIClient
import com.example.bagouli.Utils.CarPreferences
import com.example.bagouli.databinding.FragmentHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        getCars()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                showExitDialog()
            }
        })
        return binding.root
    }
    private fun getCars(){
        val call = APIClient.getService().getCars()
        call.enqueue(object : Callback<List<Cars>>{
            override fun onResponse(call: Call<List<Cars>>, response: Response<List<Cars>>) {
                val cars = response.body()
                if(!cars.isNullOrEmpty()){
                    val adapter = VehicleAdapter(cars)
                    binding.recyclerView.layoutManager = LinearLayoutManager(context)
                    binding.recyclerView.adapter = adapter
                    adapter.onClickItem = { car ->
                        val intent = Intent(context, VehicleInfoActivity::class.java)
                        intent.putExtra("cars", car)
                        CarPreferences.saveCar(car)
                        startActivity(intent)
                    }
                }else{
                    Log.e("CarRequest", "Failed to retrieve cars: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<List<Cars>>, t: Throwable) {
                Log.e("CarRequest", "Failed to retrieve cars: ${t.message}")
                t.printStackTrace()
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