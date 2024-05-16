package com.example.ecommercestore.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.ecommercestore.databinding.BestDealsRvItemBinding
import com.example.ecommercestore.models.Product

class BestDealsAdapter:Adapter<BestDealsAdapter.BestDealsHolder>(){

    inner class BestDealsHolder(private val binding:BestDealsRvItemBinding) : ViewHolder(binding.root){
        fun bind(product:Product){
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(imgBestDeal)
                tvDealProductName.text = product.name
                product.offerPercentage?.let {
                    val remainingPricePercentage = 1f - it
                    val priceAfterOffer = remainingPricePercentage*product.price
                    tvBestDealsNewPrice.text = priceAfterOffer.toString()
                    tvOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                }
                if (product.offerPercentage == null){
                    tvBestDealsNewPrice.visibility = View.INVISIBLE
                }
                tvOldPrice.text = product.price.toString()
            }
        }
    }
    private val differCallback =object : DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
    val diff = AsyncListDiffer(this,differCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestDealsHolder {
        return BestDealsHolder(
            BestDealsRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun getItemCount(): Int {
        return diff.currentList.size
    }

    override fun onBindViewHolder(holder: BestDealsHolder, position: Int) {
        val product = diff.currentList[position]
        holder.bind(product)

        holder.itemView.setOnClickListener{
            onClick?.invoke(product)
        }


    }

    var onClick:((product:Product)->Unit)?=null

}