package com.example.mylove.Adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.mylove.R

class PictureAdapter(private val itemList : MutableList<PictureData>)
    : RecyclerView.Adapter<PictureAdapter.ViewHolder>() {

    var onClickItem : ((PictureData) -> Unit)? = null
    var onLongClick : ((Int, View) -> Unit)? = null

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val image : ImageView = itemView.findViewById(R.id.listImage)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.picture_list, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.itemView.setOnClickListener {
            onClickItem?.invoke(item)
        }
        val imageUri = item.image
        if (imageUri != null && imageUri.isNotBlank()) {
            holder.image.setImageURI(Uri.parse(imageUri))
        }
        holder.itemView.setOnLongClickListener{ view ->
            onLongClick?.invoke(position, view)
            true
        }
    }
    override fun getItemCount(): Int {
        return itemList.size
    }

}