package com.example.flickrbrowser

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.row.view.*

class RVAdapter (private val main: Activity, private var list: ArrayList<Data>): RecyclerView.Adapter<RVAdapter.ItemViewHolder>() {
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

            holder.itemView.setOnClickListener{
                (main as MainActivity).DisplayImg(list[position].img, list[position].name)
            }
        }
    }

    override fun getItemCount() = list.size
}