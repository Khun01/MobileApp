package com.example.bagouli.Adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bagouli.Data.Cars
import com.example.bagouli.databinding.VehicleListBinding
import com.squareup.picasso.Picasso

class VehicleAdapter(private var carList: List<Cars>) : RecyclerView.Adapter<VehicleAdapter.ViewHolder>() {
    var onClickItem : ((Cars) -> Unit)? = null
    class ViewHolder(private val binding: VehicleListBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(cars: Cars){
            Picasso.get()
                .load(cars.image)
                .into(binding.image)
            binding.brand.text = cars.brand
            binding.price.text = cars.price_per_day.toString()
            binding.ratings.text = cars.stars.toString()
            binding.overlay.setBackgroundColor(Color.parseColor("#60000000"))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =  VehicleListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return carList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = carList[position]
        holder.bind(data)
        holder.itemView.setOnClickListener{
            onClickItem?.invoke(data)
        }
    }
}