package com.example.ecommercestore.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.ecommercestore.databinding.ColorRvItemBinding

class ColorsAdapter:Adapter<ColorsAdapter.ColorsHolder>() {

    private var selectedPosition = -1

    inner class ColorsHolder(private val binding:ColorRvItemBinding):ViewHolder(binding.root){
        fun bind(color:Int,position:Int){
            val imageColor = ColorDrawable(color)
            binding.imageColor.setImageDrawable(imageColor)

            if (position == selectedPosition){// color is selected
                binding.apply {
                    imagePicked.visibility = View.VISIBLE
                    imageShadow.visibility = View.VISIBLE
                }
            }else{// color is not selected
                binding.apply {
                    imagePicked.visibility = View.GONE
                    imageShadow.visibility = View.GONE
                }
            }
        }
    }
    private val diffUtil = object :DiffUtil.ItemCallback<Int>(){
        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }
    }

    var diff = AsyncListDiffer(this,diffUtil)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorsHolder {
        return ColorsHolder(
            ColorRvItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return diff.currentList.size
    }

    override fun onBindViewHolder(holder: ColorsHolder, position: Int) {
        val color = diff.currentList[position]
        holder.bind(color, holder.adapterPosition)

        holder.itemView.setOnClickListener{

            if (selectedPosition >=0){ // unselect the seleced color and rebuild rv from first
                notifyItemChanged(selectedPosition)
            }
            // select a new color
            selectedPosition = holder.adapterPosition
            // will cause that recyclerView will rebind item view of that position
            notifyItemChanged(selectedPosition)

            onItemClick?.invoke(color)
        }
    }
    var onItemClick:((Int) -> Unit) ?=null
}