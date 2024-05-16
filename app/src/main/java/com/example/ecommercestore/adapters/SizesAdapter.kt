package com.example.ecommercestore.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommercestore.databinding.SizeRvItemBinding

class SizesAdapter: RecyclerView.Adapter<SizesAdapter.SizesHolder>() {
    private var selectedPosition = -1

    inner class SizesHolder(private val binding: SizeRvItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(size:String,position:Int){
            binding.tvSize.text = size
            if (position == selectedPosition){// size is selected
                binding.apply {
                    imageShadow.visibility = View.VISIBLE
                }
            }else{// size is not selected
                binding.apply {
                    imageShadow.visibility = View.GONE
                }
            }
        }
    }
    private val diffUtil = object : DiffUtil.ItemCallback<String>(){
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    var diff = AsyncListDiffer(this,diffUtil)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizesHolder {
        return SizesHolder(
            SizeRvItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return diff.currentList.size
    }

    override fun onBindViewHolder(holder: SizesHolder, position: Int) {
        val size = diff.currentList[position]
        holder.bind(size, holder.adapterPosition)

        holder.itemView.setOnClickListener{

            if (selectedPosition >=0){ // unselect the seleced color and rebuild rv from first
                notifyItemChanged(selectedPosition)
            }
            // select a new color
            selectedPosition = holder.adapterPosition
            // will cause that recyclerView will rebind item view of that position
            notifyItemChanged(selectedPosition)

            onItemClick?.invoke(size)
        }
    }
    var onItemClick:((String) -> Unit) ?=null
}