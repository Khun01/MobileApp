package com.example.mylove

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mylove.Adapter.PictureAdapter
import com.example.mylove.Adapter.PictureData
import com.example.mylove.Database.PictureDatabaseHelper
import com.example.mylove.databinding.FragmentThirdAnnivBinding

class ThirdAnniv : Fragment() {

    private val itemList = mutableListOf<PictureData>()
    private lateinit var dbHelper: PictureDatabaseHelper
    private lateinit var adapter: PictureAdapter
    private lateinit var binding : FragmentThirdAnnivBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentThirdAnnivBinding.inflate(inflater, container, false)
        dbHelper = PictureDatabaseHelper(requireContext())
        initRecyclerView()
        loadVehicles()
        return binding.root
    }

    private fun initRecyclerView(){
        binding.recyclerView.layoutManager = GridLayoutManager(context, 2)
        adapter = PictureAdapter(itemList)
        binding.recyclerView.adapter = adapter
        adapter.onLongClick = { position, view ->
            showDeleteConfirmationDialog(position)
        }
        adapter.onClickItem = {
            val intent = Intent(context, PictureInfoActivity::class.java)
            intent.putExtra("pictureInfo", it)
            startActivity(intent)
        }
    }
    private fun loadVehicles(){
        try {
            itemList.clear()
            itemList.addAll(dbHelper.getPicturesByFragment("3rd Anniv"))
            adapter.notifyDataSetChanged()
        } catch (e: Exception) {
            Log.e("ThirdAnniv", "Error loading pictures: ${e.message}")
        }
    }

    private fun showDeleteConfirmationDialog(position: Int){
        val alertDialog = AlertDialog.Builder(context)
            .setTitle("Delete Item")
            .setMessage("Are you sure you want to delete this item?")
            .setPositiveButton("Delete"){ dialog, _ ->
                val delete = dbHelper.deletePicture(itemList[position].id)
                if (delete){
                    itemList.removeAt(position)
                    adapter.notifyItemRemoved(position)
                    Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel"){ dialog, _ ->
                dialog.dismiss()
            }
            .create()
        alertDialog.show()
    }
}