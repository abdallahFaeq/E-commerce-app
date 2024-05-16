package com.example.ecommercestore.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.ecommercestore.databinding.ViewPagerItemBinding

class ViewPager2ImagesAdapter:RecyclerView.Adapter<ViewPager2ImagesAdapter.ViewPagerImagesHolder>() {

    inner class ViewPagerImagesHolder (private val binding:ViewPagerItemBinding): ViewHolder(binding.root){
        fun bind(imagePath:String){
            Glide.with(itemView).load(imagePath).into(binding.imageProductDetails)
        }
    }
    private var diffUtil =object :DiffUtil.ItemCallback<String>(){
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    var diff = AsyncListDiffer(this,diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerImagesHolder {
        return ViewPagerImagesHolder(
            ViewPagerItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return diff.currentList.size
    }

    override fun onBindViewHolder(holder: ViewPagerImagesHolder, position: Int) {
        val image = diff.currentList[position]

        holder.bind(image)
    }


}