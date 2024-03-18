package com.example.bagouli.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bagouli.Data.Reservation
import com.example.bagouli.databinding.ReservationListBinding
import com.squareup.picasso.Picasso

class ReservationAdapter(private var reservationList : List<Reservation>)
    : RecyclerView.Adapter<ReservationAdapter.ViewHolder>() {

    var onClickItem : ((Reservation) -> Unit)? = null
    var onMenuDotClick : ((position: Int, view: View, reservation: Reservation) -> Unit)? = null
    class ViewHolder(private val binding: ReservationListBinding): RecyclerView.ViewHolder(binding.root){

        val menu = binding.menuDot
        fun bind(reservation: Reservation){
            Picasso.get().load(reservation.car.image).into(binding.image)
            binding.brand.text = reservation.car.brand
            binding.model.text = reservation.car.model
            binding.totalPrice.text = reservation.total_price.toString()
            binding.status.text = reservation.status
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ReservationListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return reservationList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = reservationList[position]
        holder.bind(data)
        holder.itemView.setOnClickListener {
            onClickItem?.invoke(data)
        }
        holder.menu.setOnClickListener { view ->
            onMenuDotClick?.invoke(position, view, data)
        }
    }

    fun removeReservation(position: Int){
        if (position in reservationList.indices){
            reservationList = reservationList.toMutableList().apply { removeAt(position) }
            notifyItemRemoved(position)
        }
    }
}