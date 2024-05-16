package com.example.ecommercestore.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.ecommercestore.databinding.SpecialRvItemBinding
import com.example.ecommercestore.models.Product

class SpecialProductsAdapter : Adapter<SpecialProductsAdapter.SpecialProductsHodler>(){

    inner class SpecialProductsHodler(private val binding:SpecialRvItemBinding):ViewHolder(binding.root){
        fun bind(product:Product){
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(imgSpecialProducts)
                tvSpecialProductsName.text = product.name
                tvSpecialProductsPrice.text = product.price.toString()
            }
        }
    }

    // refresh item that updated only not all list of items
    private val diffCallback = object : DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            // compare update item only
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            // compare whole object
            return oldItem == newItem
        }
    }
    // responsible for update list and get the list
    val diff = AsyncListDiffer(this,diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecialProductsHodler {
        return SpecialProductsHodler(
            SpecialRvItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return diff.currentList.size
    }

    override fun onBindViewHolder(holder: SpecialProductsHodler, position: Int) {
        // bind items
        val product = diff.currentList[position]
        holder.bind(product)

        holder.itemView.setOnClickListener{
            onClick?.invoke(product)
        }
    }
    var onClick:((product:Product)-> Unit)?=null
}