package com.example.ecommercestore.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.ecommercestore.databinding.BillingProductsRvItemBinding
import com.example.ecommercestore.helper.getProductPrice
import com.example.ecommercestore.models.CartProduct

class BillingAdapter :Adapter<BillingAdapter.BillingHolder>(){
    inner class BillingHolder(val binding:BillingProductsRvItemBinding):ViewHolder(binding.root){
        fun bind(billingCartProduct:CartProduct){
            binding.apply {
                Glide.with(itemView).load(billingCartProduct.product.images[0]).into(imageCartProduct)
                tvProductCartName.text = billingCartProduct.product.name
                tvBillingProductQuantity.text = billingCartProduct.quantity.toString()

                billingCartProduct.product.offerPercentage?.let {
                    val priceAfterPercentage = it.getProductPrice(billingCartProduct.product.price)
                    tvProductCartPrice.text = "$ ${String.format("%.2f",priceAfterPercentage)}"
                }
                imageCartProductColor.setImageDrawable(ColorDrawable(billingCartProduct.selectedColor?: Color.TRANSPARENT))
                tvCartProductSize.text = billingCartProduct.selectedSize?:"".also {
                    imageCartProductSize.setImageDrawable(ColorDrawable(Color.TRANSPARENT))
                }
            }
        }
    }

    private var diffUtil = object : DiffUtil.ItemCallback<CartProduct>(){
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem.product == newItem.product
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem == newItem
        }
    }

    var diff = AsyncListDiffer(this,diffUtil)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillingHolder {
        return BillingHolder(
            BillingProductsRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun getItemCount(): Int {
        return diff.currentList.size
    }

    override fun onBindViewHolder(holder: BillingHolder, position: Int) {
        var billingProduct = diff.currentList[position]

        holder.bind(billingProduct)
    }
}