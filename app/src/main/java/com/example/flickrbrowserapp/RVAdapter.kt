package com.example.flickrbrowserapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.row.view.*

class RVAdapter (private var list: ArrayList<Data>): RecyclerView.Adapter<RVAdapter.ItemViewHolder>() {
    class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.row,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val data = list[position]

        holder.itemView.apply {
            Glide.with(context).load(data.img).into(image)

            imageName.text = data.name

//            holder.itemView.setOnClickListener{
//                val data = Data(list[position].img, list[position].name)
//                val intent = Intent(holder.itemView.context, DisplayData::class.java)
//                intent.putExtra("displayData",data)
//                holder.itemView.context.startActivity(intent)
//            }
        }
    }

    override fun getItemCount() = list.size
}